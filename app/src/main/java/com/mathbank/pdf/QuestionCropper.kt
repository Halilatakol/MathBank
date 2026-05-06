package com.mathbank.pdf

import android.graphics.Bitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

data class QuestionRegion(
    val questionNumber: Int,
    val topY: Int,
    val bottomY: Int,
    val bitmap: Bitmap
)

class QuestionCropper {

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * OCR ile sayfadaki soru sınırlarını tespit eder, her soruyu ayrı bitmap olarak kırpar.
     * Soru numaralarını "1)", "2.", "1-", "Soru 1" formatlarında tanır.
     */
    suspend fun cropQuestions(pageBitmap: Bitmap): List<QuestionRegion> {
        val visionText = recognizeText(pageBitmap)

        // Soru başlangıç noktalarını bul: (soru_no, topY_piksel)
        val boundaries = mutableListOf<Pair<Int, Int>>()

        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val lineText = line.text.trim()
                val lineTop = line.boundingBox?.top ?: continue
                val qNum = detectQuestionNumber(lineText) ?: continue
                // Aynı numarayı tekrar ekleme
                if (boundaries.none { it.first == qNum }) {
                    boundaries.add(Pair(qNum, lineTop))
                }
            }
        }

        if (boundaries.isEmpty()) {
            // OCR soru bulamazsa boş döndür — AI analizi yapılacak
            return emptyList()
        }

        // Y koordinatına göre sırala
        boundaries.sortBy { it.second }

        val regions = mutableListOf<QuestionRegion>()
        val padding = 20 // piksel padding - soru kesilmesin

        for (i in boundaries.indices) {
            val (qNum, topY) = boundaries[i]

            // Her sorunun alt sınırı: bir sonraki sorunun üstü - 5px
            // Son soru: sayfanın sonuna kadar
            val bottomY = if (i < boundaries.size - 1) {
                boundaries[i + 1].second - 5
            } else {
                pageBitmap.height
            }

            val cropTop = (topY - padding).coerceAtLeast(0)
            val cropBottom = (bottomY + padding).coerceAtMost(pageBitmap.height)
            val cropHeight = (cropBottom - cropTop).coerceAtLeast(50)

            val croppedBitmap = Bitmap.createBitmap(
                pageBitmap, 0, cropTop, pageBitmap.width, cropHeight
            )

            regions.add(QuestionRegion(
                questionNumber = qNum,
                topY = cropTop,
                bottomY = cropBottom,
                bitmap = croppedBitmap
            ))
        }

        return regions
    }

    /**
     * Soru numarasını tespit et.
     * Desteklenen formatlar: "1)", "1.", "1-", "Soru 1", "Q1"
     */
    private fun detectQuestionNumber(text: String): Int? {
        // "1)" veya "1." veya "1-" formatı - en yaygın
        val pattern1 = Regex("""^(\d{1,2})[.)\\-]\s*""")
        pattern1.find(text)?.let {
            val num = it.groupValues[1].toIntOrNull() ?: return null
            if (num in 1..99) return num
        }

        // "Soru 1" veya "SORU 1" formatı
        val pattern2 = Regex("""(?i)^soru\s+(\d{1,2})""")
        pattern2.find(text)?.let {
            return it.groupValues[1].toIntOrNull()
        }

        // "Q1" veya "Q 1" formatı
        val pattern3 = Regex("""(?i)^q\s*(\d{1,2})\b""")
        pattern3.find(text)?.let {
            return it.groupValues[1].toIntOrNull()
        }

        return null
    }

    private suspend fun recognizeText(bitmap: Bitmap) =
        suspendCancellableCoroutine { cont ->
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { text -> cont.resume(text) }
                .addOnFailureListener { e -> cont.resumeWithException(e) }
        }
}
