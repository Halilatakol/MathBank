package com.mathbank.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mathbank.data.db.Converters

@Entity(tableName = "questions")
@TypeConverters(Converters::class)
data class Question(
    @PrimaryKey
    val id: String,

    // Soru içeriği
    val text: String,
    val imageData: ByteArray? = null,         // Soru görseli (kırpılmış)
    val fullPageImageData: ByteArray? = null,  // Tam sayfa görseli

    // Sınıflandırma (AI tarafından otomatik)
    val topic: String,           // Ana konu: Geometri, Cebir, vb.
    val subtopic: String,        // Alt konu: Üçgenler, Denklemler, vb.
    val difficulty: Difficulty,  // Kolay / Orta / Zor

    // Çoktan seçmeli
    val options: List<String> = emptyList(),   // A, B, C, D şıkları
    val correctAnswer: String? = null,         // Doğru şık

    // Kaynak bilgisi
    val sourcePdf: String,
    val pageNumber: Int,

    // Meta
    val createdAt: Long = System.currentTimeMillis(),
    val hasFigure: Boolean = false,
    val questionType: QuestionType = QuestionType.MULTIPLE_CHOICE
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Question) return false
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}

enum class Difficulty(val displayName: String, val emoji: String) {
    EASY("Kolay", "🟢"),
    MEDIUM("Orta", "🟡"),
    HARD("Zor", "🔴");

    companion object {
        fun fromString(value: String): Difficulty {
            return when (value.lowercase().trim()) {
                "kolay", "easy", "basit" -> EASY
                "orta", "medium", "normal" -> MEDIUM
                "zor", "hard", "difficult", "ileri" -> HARD
                else -> MEDIUM
            }
        }
    }
}

enum class QuestionType(val displayName: String) {
    MULTIPLE_CHOICE("Çoktan Seçmeli"),
    OPEN_ENDED("Açık Uçlu"),
    TRUE_FALSE("Doğru/Yanlış")
}
