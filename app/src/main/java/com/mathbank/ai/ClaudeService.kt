package com.mathbank.ai

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.mathbank.data.model.BoundingBox
import com.mathbank.data.model.ClaudeAnalysisResult
import com.mathbank.data.model.ExtractedQuestion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

class ClaudeService(
    private val apiKey: String,
    private val model: String = MODEL_OPENROUTER
) {
    companion object {
        private const val TAG = "AIService"
        const val MODEL_OPENROUTER = "openrouter"
        const val MODEL_GEMINI = "gemini"
        private const val OPENROUTER_URL = "https://openrouter.ai/api/v1/chat/completions"
        private const val OPENROUTER_MODEL = "openrouter/free"
        private const val GEMINI_MODEL = "gemini-2.0-flash"
        private const val GEMINI_BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"
        private const val MAX_RETRIES = 3
    }

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val gson = Gson()

    suspend fun analyzePage(
        bitmap: Bitmap,
        pageNumber: Int,
        pdfName: String
    ): Result<ClaudeAnalysisResult> = withContext(Dispatchers.IO) {
        val base64Image = bitmapToBase64(bitmap)
        val imgH = bitmap.height
        val imgW = bitmap.width
        val prompt = buildPrompt(imgW, imgH)
        var lastError: Exception = Exception("Bilinmeyen hata")

        for (attempt in 0 until MAX_RETRIES) {
            try {
                val request = if (model == MODEL_GEMINI) {
                    buildGeminiRequest(base64Image, prompt)
                } else {
                    buildOpenRouterRequest(base64Image, prompt)
                }

                val response = client.newCall(request).execute()
                val body = response.body?.string() ?: ""

                when {
                    response.code == 429 -> {
                        val wait = (attempt + 1) * 60L
                        Log.w(TAG, "Rate limit, ${wait}s bekleniyor...")
                        delay(wait * 1000)
                        lastError = Exception("Rate limit asildi")
                        continue
                    }
                    response.code == 503 || response.code == 502 -> {
                        val wait = (attempt + 1) * 20L
                        delay(wait * 1000)
                        lastError = Exception("Servis gecici kapali")
                        continue
                    }
                    !response.isSuccessful -> {
                        return@withContext Result.failure(
                            Exception("API Hatasi: ${response.code} - $body")
                        )
                    }
                    else -> {
                        val result = if (model == MODEL_GEMINI) {
                            parseGeminiResponse(body, imgW, imgH)
                        } else {
                            parseOpenRouterResponse(body, imgW, imgH)
                        }
                        return@withContext Result.success(result)
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Deneme ${attempt + 1} hatasi: ${e.message}")
                lastError = e
                if (attempt < MAX_RETRIES - 1) delay(10_000)
            }
        }
        Result.failure(lastError)
    }

    private fun buildGeminiRequest(base64Image: String, prompt: String): Request {
        val url = "$GEMINI_BASE_URL/$GEMINI_MODEL:generateContent?key=$apiKey"
        val bodyMap = mapOf(
            "contents" to listOf(
                mapOf(
                    "parts" to listOf(
                        mapOf(
                            "inline_data" to mapOf(
                                "mime_type" to "image/jpeg",
                                "data" to base64Image
                            )
                        ),
                        mapOf("text" to prompt)
                    )
                )
            ),
            "generationConfig" to mapOf(
                "temperature" to 0.1,
                "maxOutputTokens" to 8192,
                "responseMimeType" to "application/json"
            ),
            "safetySettings" to listOf(
                mapOf("category" to "HARM_CATEGORY_HARASSMENT", "threshold" to "BLOCK_NONE"),
                mapOf("category" to "HARM_CATEGORY_HATE_SPEECH", "threshold" to "BLOCK_NONE"),
                mapOf("category" to "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold" to "BLOCK_NONE"),
                mapOf("category" to "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold" to "BLOCK_NONE")
            )
        )
        return Request.Builder()
            .url(url)
            .addHeader("Content-Type", "application/json")
            .post(gson.toJson(bodyMap).toRequestBody("application/json".toMediaType()))
            .build()
    }

    private fun buildOpenRouterRequest(base64Image: String, prompt: String): Request {
        val bodyMap = mapOf(
            "model" to OPENROUTER_MODEL,
            "messages" to listOf(
                mapOf(
                    "role" to "user",
                    "content" to listOf(
                        mapOf(
                            "type" to "image_url",
                            "image_url" to mapOf(
                                "url" to "data:image/jpeg;base64,$base64Image"
                            )
                        ),
                        mapOf("type" to "text", "text" to prompt)
                    )
                )
            )
        )
        return Request.Builder()
            .url(OPENROUTER_URL)
            .addHeader("Authorization", "Bearer $apiKey")
            .addHeader("Content-Type", "application/json")
            .addHeader("HTTP-Referer", "https://github.com/Halilatakol/MathBank")
            .addHeader("X-Title", "MathBank")
            .post(gson.toJson(bodyMap).toRequestBody("application/json".toMediaType()))
            .build()
    }

    /**
     * Bu prompt tek bir soru gorselini analiz eder (OCR tarafindan kirpilmis).
     * Sorunun tam icerigi, konusu, zorlugu ve siklari cikarilir.
     */
    private fun buildPrompt(imgW: Int, imgH: Int): String = """
Sen bir matematik sorusu analiz uzmanisın.
Bu goruntu bir matematik sorusunun gorselidir (${imgW}x${imgH} piksel).

Bu goruntudeki TEK soruyu analiz et ve JSON formatinda don:

- questionNumber: soru numarasi (gorsel icerisindeki numara, yoksa 1)
- text: sorunun TAM metni, hicbir sey atlama
  * Matematiksel semboller: √ (kok), π (pi), ² (kare), ³ (kup), ≤ ≥ (esitsizlik)
  * Kesirleri: 3/2 formatinda yaz
  * Ustel ifadeler: x^2 formatinda yaz
- topic: Cebir|Geometri|Analitik Geometri|Trigonometri|Analiz|Sayilar|Olasilik|Kombinatorik|Dizi-Seri|Logaritma|Fonksiyonlar
- subtopic: cok spesifik alt konu (ornek: "Koni Hacmi", "Ikinci Dereceden Denklem Kokleri")
- difficulty: EASY (tek adim)|MEDIUM (2-3 adim)|HARD (4+ adim, multi-konu)
- options: siklar varsa ["A) ...", "B) ...", "C) ...", "D) ...", "E) ..."], yoksa []
- correctAnswer: PDF'de cevap gozukuyorsa yaz, yoksa null
- hasFigure: goruntude geometrik sekil, koordinat sistemi veya grafik var mi (true/false)
- questionType: MULTIPLE_CHOICE|OPEN_ENDED|TRUE_FALSE
- topPx: sorunun goruntudeki en ust noktasi (piksel, 0-$imgH)
- bottomPx: sorunun goruntudeki en alt noktasi (piksel, 0-$imgH)

SADECE JSON dondur, baska hicbir sey yazma:
{"questions":[{"questionNumber":1,"text":"Sorunun tam metni...","topic":"Geometri","subtopic":"Koni Hacmi","difficulty":"MEDIUM","options":["A) 67","B) 72","C) 74","D) 76","E) 78"],"correctAnswer":null,"hasFigure":true,"questionType":"MULTIPLE_CHOICE","topPx":0,"bottomPx":$imgH}]}

Gorsel bos veya soru yoksa: {"questions":[]}
    """.trimIndent()

    private fun parseGeminiResponse(body: String, w: Int, h: Int): ClaudeAnalysisResult {
        return try {
            val json = JsonParser.parseString(body).asJsonObject
            val text = json.getAsJsonArray("candidates")
                ?.get(0)?.asJsonObject
                ?.getAsJsonObject("content")
                ?.getAsJsonArray("parts")
                ?.get(0)?.asJsonObject
                ?.get("text")?.asString ?: "{\"questions\":[]}"
            parseQuestions(text, w, h)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini parse hatasi: ${e.message}")
            ClaudeAnalysisResult(questions = emptyList())
        }
    }

    private fun parseOpenRouterResponse(body: String, w: Int, h: Int): ClaudeAnalysisResult {
        return try {
            val json = JsonParser.parseString(body).asJsonObject
            val text = json.getAsJsonArray("choices")
                ?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")
                ?.get("content")?.asString ?: "{\"questions\":[]}"
            parseQuestions(text, w, h)
        } catch (e: Exception) {
            Log.e(TAG, "OpenRouter parse hatasi: ${e.message}")
            ClaudeAnalysisResult(questions = emptyList())
        }
    }

    private fun parseQuestions(content: String, imgW: Int, imgH: Int): ClaudeAnalysisResult {
        return try {
            val clean = content.replace("```json", "").replace("```", "").trim()
            val parsed = JsonParser.parseString(clean).asJsonObject
            val arr = parsed.getAsJsonArray("questions")
                ?: return ClaudeAnalysisResult(questions = emptyList())

            val questions = arr.mapNotNull { elem ->
                try {
                    val q = elem.asJsonObject
                    val topPx = q.get("topPx")?.asInt ?: 0
                    val bottomPx = q.get("bottomPx")?.asInt ?: imgH
                    val bbox = BoundingBox(
                        top = (topPx.toFloat() / imgH).coerceIn(0f, 1f),
                        left = 0f,
                        bottom = (bottomPx.toFloat() / imgH).coerceIn(0f, 1f),
                        right = 1f
                    )
                    ExtractedQuestion(
                        questionNumber = q.get("questionNumber")?.asInt ?: 1,
                        text = q.get("text")?.asString ?: "",
                        topic = q.get("topic")?.asString ?: "Genel Matematik",
                        subtopic = q.get("subtopic")?.asString ?: "",
                        difficulty = q.get("difficulty")?.asString ?: "MEDIUM",
                        options = q.getAsJsonArray("options")?.map { it.asString } ?: emptyList(),
                        correctAnswer = q.get("correctAnswer")?.takeIf { !it.isJsonNull }?.asString,
                        hasFigure = q.get("hasFigure")?.asBoolean ?: false,
                        questionType = q.get("questionType")?.asString ?: "MULTIPLE_CHOICE",
                        boundingBox = bbox
                    )
                } catch (e: Exception) {
                    Log.w(TAG, "Soru parse hatasi: ${e.message}")
                    null
                }
            }
            ClaudeAnalysisResult(questions = questions)
        } catch (e: Exception) {
            Log.e(TAG, "JSON parse hatasi: ${e.message}")
            ClaudeAnalysisResult(questions = emptyList())
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDim = 1568
        val scaled = if (bitmap.width > maxDim || bitmap.height > maxDim) {
            val scale = minOf(maxDim.toFloat() / bitmap.width, maxDim.toFloat() / bitmap.height)
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap
        val out = ByteArrayOutputStream()
        scaled.compress(Bitmap.CompressFormat.JPEG, 85, out)
        return Base64.encodeToString(out.toByteArray(), Base64.NO_WRAP)
    }

    fun cropQuestionImage(pageBitmap: Bitmap, boundingBox: BoundingBox): Bitmap {
        val pad = 8
        val top = ((boundingBox.top * pageBitmap.height).toInt() - pad).coerceAtLeast(0)
        val bottom = ((boundingBox.bottom * pageBitmap.height).toInt() + pad)
            .coerceAtMost(pageBitmap.height)
        val height = (bottom - top).coerceAtLeast(1)
        return Bitmap.createBitmap(pageBitmap, 0, top, pageBitmap.width, height)
    }
}
