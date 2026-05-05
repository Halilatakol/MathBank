package com.mathbank.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.mathbank.data.db.Converters

@Entity(tableName = "tests")
@TypeConverters(Converters::class)
data class Test(
    @PrimaryKey
    val id: String,
    val name: String,
    val questionIds: List<String>,
    val topics: List<String>,
    val difficulties: List<Difficulty>,
    val createdAt: Long = System.currentTimeMillis(),
    val totalTime: Int = 0,        // dakika (0 = limitsiz)
    val isCompleted: Boolean = false,
    val score: Int = 0,            // yüzde
    val completedAt: Long? = null
)

@Entity(tableName = "test_answers")
data class TestAnswer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val testId: String,
    val questionId: String,
    val selectedAnswer: String?,
    val isCorrect: Boolean,
    val timeSpent: Int = 0         // saniye
)

// Claude API yanıt modelleri
data class ClaudeAnalysisResult(
    val questions: List<ExtractedQuestion>
)

data class ExtractedQuestion(
    val questionNumber: Int,
    val text: String,
    val topic: String,
    val subtopic: String,
    val difficulty: String,
    val options: List<String>,
    val correctAnswer: String?,
    val hasFigure: Boolean,
    val questionType: String,
    // Görseldeki koordinatlar (0.0 - 1.0 arası oransal)
    val boundingBox: BoundingBox?
)

data class BoundingBox(
    val top: Float,
    val left: Float,
    val bottom: Float,
    val right: Float
)

// Filtre modeli
data class QuestionFilter(
    val topics: List<String> = emptyList(),
    val subtopics: List<String> = emptyList(),
    val difficulties: List<Difficulty> = emptyList(),
    val searchQuery: String = "",
    val hasFigureOnly: Boolean = false
)

// İstatistik modeli
data class TopicStats(
    val topic: String,
    val subtopic: String,
    val totalCount: Int,
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int
)

data class ProcessingProgress(
    val currentPage: Int,
    val totalPages: Int,
    val questionsFound: Int,
    val status: ProcessingStatus
)

enum class ProcessingStatus {
    PREPARING,
    PROCESSING,
    SAVING,
    COMPLETED,
    ERROR
}
