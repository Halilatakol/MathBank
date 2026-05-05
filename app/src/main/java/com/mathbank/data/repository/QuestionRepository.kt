package com.mathbank.data.repository

import android.graphics.Bitmap
import com.mathbank.data.db.AppDatabase
import com.mathbank.data.model.*
import java.io.ByteArrayOutputStream
import java.util.UUID

class QuestionRepository(private val db: AppDatabase) {

    private val questionDao = db.questionDao()
    private val testDao = db.testDao()

    // ──────────────── QUESTIONS ────────────────

    suspend fun saveQuestion(
        extracted: ExtractedQuestion,
        pageBitmap: Bitmap?,
        croppedBitmap: Bitmap?,
        sourcePdf: String,
        pageNumber: Int
    ): Question {
        val question = Question(
            id = UUID.randomUUID().toString(),
            text = extracted.text,
            imageData = croppedBitmap?.toByteArray(),
            fullPageImageData = pageBitmap?.toByteArray(quality = 60),
            topic = extracted.topic,
            subtopic = extracted.subtopic,
            difficulty = Difficulty.fromString(extracted.difficulty),
            options = extracted.options,
            correctAnswer = extracted.correctAnswer,
            sourcePdf = sourcePdf,
            pageNumber = pageNumber,
            hasFigure = extracted.hasFigure,
            questionType = when (extracted.questionType) {
                "OPEN_ENDED" -> QuestionType.OPEN_ENDED
                "TRUE_FALSE" -> QuestionType.TRUE_FALSE
                else -> QuestionType.MULTIPLE_CHOICE
            }
        )
        questionDao.insert(question)
        return question
    }

    suspend fun saveQuestions(questions: List<Question>) {
        questionDao.insertAll(questions)
    }

    suspend fun getAllQuestions(): List<Question> = questionDao.getAll()

    suspend fun getFilteredQuestions(filter: QuestionFilter): List<Question> {
        return questionDao.getFiltered(
            topics = filter.topics,
            topicsEmpty = if (filter.topics.isEmpty()) 1 else 0,
            difficulties = filter.difficulties.map { it.name },
            difficultiesEmpty = if (filter.difficulties.isEmpty()) 1 else 0,
            query = filter.searchQuery,
            figurOnly = if (filter.hasFigureOnly) 1 else 0
        )
    }

    suspend fun getAllTopics(): List<String> = questionDao.getAllTopics()

    suspend fun getSubtopics(topic: String): List<String> = questionDao.getSubtopics(topic)

    suspend fun getTotalCount(): Int = questionDao.getTotalCount()

    suspend fun getCountByDifficulty(difficulty: Difficulty): Int =
        questionDao.getCountByDifficulty(difficulty.name)

    suspend fun getTopicStats(): List<TopicStats> {
        return questionDao.getTopicStats().map {
            TopicStats(
                topic = it.topic,
                subtopic = it.subtopic,
                totalCount = it.totalCount,
                easyCount = it.easyCount,
                mediumCount = it.mediumCount,
                hardCount = it.hardCount
            )
        }
    }

    suspend fun deleteQuestion(question: Question) = questionDao.delete(question)

    suspend fun updateQuestion(question: Question) = questionDao.update(question)

    // ──────────────── TESTS ────────────────

    suspend fun createTest(
        name: String,
        questionIds: List<String>,
        topics: List<String>,
        difficulties: List<Difficulty>,
        totalTime: Int = 0
    ): Test {
        val test = Test(
            id = UUID.randomUUID().toString(),
            name = name,
            questionIds = questionIds,
            topics = topics,
            difficulties = difficulties,
            totalTime = totalTime
        )
        testDao.insertTest(test)
        return test
    }

    suspend fun getRandomQuestions(
        topics: List<String>,
        difficulties: List<Difficulty>,
        count: Int
    ): List<Question> {
        return questionDao.getRandomQuestions(
            topics = topics,
            topicsEmpty = if (topics.isEmpty()) 1 else 0,
            difficulties = difficulties.map { it.name },
            difficultiesEmpty = if (difficulties.isEmpty()) 1 else 0,
            limit = count
        )
    }

    suspend fun getAllTests(): List<Test> = testDao.getAllTests()

    suspend fun getTestById(id: String): Test? = testDao.getTestById(id)

    suspend fun getQuestionsByIds(ids: List<String>): List<Question> =
        questionDao.getByIds(ids)

    suspend fun saveTestResult(
        testId: String,
        answers: List<TestAnswer>,
        score: Int
    ) {
        testDao.insertAnswers(answers)
        val test = testDao.getTestById(testId) ?: return
        testDao.updateTest(test.copy(
            isCompleted = true,
            score = score,
            completedAt = System.currentTimeMillis()
        ))
    }

    suspend fun getAnswersForTest(testId: String): List<TestAnswer> =
        testDao.getAnswersForTest(testId)

    suspend fun deleteTest(test: Test) {
        testDao.deleteAnswersForTest(test.id)
        testDao.deleteTest(test)
    }

    // ──────────────── HELPERS ────────────────

    private fun Bitmap.toByteArray(quality: Int = 80): ByteArray {
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG, quality, stream)
        return stream.toByteArray()
    }
}
