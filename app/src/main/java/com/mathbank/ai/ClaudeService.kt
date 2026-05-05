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

class ClaudeService(private val apiKey: String) {

    companion object {
        private const val TAG = "OpenRouterService"
        private const val API_URL = "https://openrouter.ai/api/v1/chat/completions"
        private const val MODEL = "openrouter/free"
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
        val prompt = buildPrompt(pageNumber)
        val requestBody = buildRequestBody(base64Image, prompt)

        var lastError: Exception = Exception("Bilinmeyen hata")

        for (attempt in 0 until MAX_RETRIES) {
            try {
                val request = Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer $apiKey")
                    .addHeader("Content-Type", "application/json")
                    .addHeader("HTTP-Referer", "https://github.com/Halilatakol/MathBank")
                    .addHeader("X-Title", "MathBank")
                    .post(requestBody.toRequestBody("application/json".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string() ?: ""

                when {
                    response.code == 429 -> {
                        // Rate limit - bekle ve tekrar dene
                        val waitSeconds = (attempt + 1) * 30L
                        Log.w(TAG, "Rate limit (429), ${waitSeconds}s bekleniyor... (deneme ${attempt + 1}/$MAX_RETRIES)")
                        delay(waitSeconds * 1000)
                        lastError = Exception("Rate limit asıldı")
                        continue
                    }
                    response.code == 503 || response.code == 502 -> {
                        // Servis geçici kapalı
                        val waitSeconds = (attempt + 1) * 15L
                        Log.w(TAG, "Servis hatası (${response.code}), ${waitSeconds}s bekleniyor...")
                        delay(waitSeconds * 1000)
                        lastError = Exception("Servis gecici kapali: ${response.code}")
                        continue
                    }
                    !response.isSuccessful -> {
                        Log.e(TAG, "API Hatası ${response.code}: $responseBody")
                        return@withContext Result.failure(
                            Exception("API Hatasi: ${response.code} - $responseBody")
                        )
                    }
                    else -> {
                        // Basarili
                        return@withContext Result.success(parseResponse(responseBody))
                    }
                }

            } catch (e: Exception) {
                Log.e(TAG, "Deneme ${attempt + 1} hatasi: ${e.message}")
                lastError = e
                if (attempt < MAX_RETRIES - 1) {
                    delay(10_000)
                }
            }
        }

        Result.failure(lastError)
    }

    private fun buildPrompt(pageNumber: Int): String = """
Sen bir matematik sorusu analiz uzmanisın. Bu goruntu bir kitabin $pageNumber. sayfasidir.

Sayfadaki TUM sorulari tespit et. Her turlu duzeni destekle (numarali, cizgili, bosluklu, karma).

Her soru icin JSON dondur:
- questionNumber: soru numarasi (int)
- text: soru metninin tamami
- topic: Cebir|Geometri|Analitik Geometri|Trigonometri|Analiz|Sayilar|Olasilik|Kombinatorik|Dizi-Seri|Logaritma|Fonksiyonlar
- subtopic: spesifik alt konu
- difficulty: EASY|MEDIUM|HARD
- options: siklar varsa liste ["A) ...", "B) ..."], yoksa []
- correctAnswer: cevap varsa yaz yoksa null
- hasFigure: sekil veya grafik var mi (true/false)
- questionType: MULTIPLE_CHOICE|OPEN_ENDED|TRUE_FALSE
- boundingBox: {"top":0.0,"left":0.0,"bottom":1.0,"right":1.0}
- Matematik sembollerini doğru yaz: kök işareti için √, pi için π, 
  kesir için / kullan. Örnek: √3, 2π, 3/2
- Soru metnini eksiksiz ve doğru yaz, hiçbir karakteri atlama
SADECE JSON dondur, baska hicbir sey yazma:
{"questions":[...]}

Sayfa bossa veya soru yoksa: {"questions":[]}
    """.trimIndent()

    private fun buildRequestBody(base64Image: String, prompt: String): String {
        val body = mapOf(
            "model" to MODEL,
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
                        mapOf(
                            "type" to "text",
                            "text" to prompt
                        )
                    )
                )
            )
        )
        return gson.toJson(body)
    }

    private fun parseResponse(responseBody: String): ClaudeAnalysisResult {
        return try {
            val json = JsonParser.parseString(responseBody).asJsonObject
            val content = json.getAsJsonArray("choices")
                ?.get(0)?.asJsonObject
                ?.getAsJsonObject("message")
                ?.get("content")?.asString ?: "{\"questions\":[]}"

            val cleanJson = content
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val parsed = JsonParser.parseString(cleanJson).asJsonObject
            val questionsArray = parsed.getAsJsonArray("questions")
                ?: return ClaudeAnalysisResult(questions = emptyList())

            val questions = questionsArray.mapNotNull { elem ->
                try {
                    val q = elem.asJsonObject
                    val bbox = q.get("boundingBox")?.takeIf { !it.isJsonNull }?.let {
                        val bb = it.asJsonObject
                        BoundingBox(
                            top = bb.get("top")?.asFloat ?: 0f,
                            left = bb.get("left")?.asFloat ?: 0f,
                            bottom = bb.get("bottom")?.asFloat ?: 1f,
                            right = bb.get("right")?.asFloat ?: 1f
                        )
                    }
                    ExtractedQuestion(
                        questionNumber = q.get("questionNumber")?.asInt ?: 0,
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
            Log.e(TAG, "Response parse hatasi: ${e.message}")
            ClaudeAnalysisResult(questions = emptyList())
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val maxDimension = 1568
        val scaledBitmap = if (bitmap.width > maxDimension || bitmap.height > maxDimension) {
            val scale = minOf(
                maxDimension.toFloat() / bitmap.width,
                maxDimension.toFloat() / bitmap.height
            )
            Bitmap.createScaledBitmap(
                bitmap,
                (bitmap.width * scale).toInt(),
                (bitmap.height * scale).toInt(),
                true
            )
        } else bitmap

        val outputStream = ByteArrayOutputStream()
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }

    fun cropQuestionImage(pageBitmap: Bitmap, boundingBox: BoundingBox): Bitmap {
        val x = (boundingBox.left * pageBitmap.width).toInt().coerceAtLeast(0)
        val y = (boundingBox.top * pageBitmap.height).toInt().coerceAtLeast(0)
        val width = ((boundingBox.right - boundingBox.left) * pageBitmap.width).toInt()
            .coerceAtMost(pageBitmap.width - x).coerceAtLeast(1)
        val height = ((boundingBox.bottom - boundingBox.top) * pageBitmap.height).toInt()
            .coerceAtMost(pageBitmap.height - y).coerceAtLeast(1)
        return Bitmap.createBitmap(pageBitmap, x, y, width, height)
    }
}
