package com.mathbank.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mathbank.data.model.*

// ──────────────────────────────────────────────
// TYPE CONVERTERS
// ──────────────────────────────────────────────
class Converters {
    private val gson = Gson()

    @TypeConverter fun difficultyToString(d: Difficulty) = d.name
    @TypeConverter fun stringToDifficulty(s: String) = Difficulty.valueOf(s)

    @TypeConverter fun questionTypeToString(t: QuestionType) = t.name
    @TypeConverter fun stringToQuestionType(s: String) = QuestionType.valueOf(s)

    @TypeConverter fun listToJson(list: List<String>): String = gson.toJson(list)
    @TypeConverter fun jsonToList(json: String): List<String> =
        gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()

    @TypeConverter fun difficultyListToJson(list: List<Difficulty>): String =
        gson.toJson(list.map { it.name })
    @TypeConverter fun jsonToDifficultyList(json: String): List<Difficulty> {
        val names: List<String> = gson.fromJson(json, object : TypeToken<List<String>>() {}.type) ?: emptyList()
        return names.map { Difficulty.valueOf(it) }
    }
}

// ──────────────────────────────────────────────
// QUESTION DAO
// ──────────────────────────────────────────────
@Dao
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(questions: List<Question>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(question: Question)

    @Update
    suspend fun update(question: Question)

    @Delete
    suspend fun delete(question: Question)

    @Query("DELETE FROM questions WHERE id = :id")
    suspend fun deleteById(id: String)

    @Query("SELECT * FROM questions ORDER BY createdAt DESC")
    suspend fun getAll(): List<Question>

    @Query("SELECT * FROM questions WHERE id = :id")
    suspend fun getById(id: String): Question?

    @Query("""
        SELECT * FROM questions 
        WHERE (:topicsEmpty = 1 OR topic IN (:topics))
          AND (:difficultiesEmpty = 1 OR difficulty IN (:difficulties))
          AND (:query = '' OR text LIKE '%' || :query || '%' OR topic LIKE '%' || :query || '%' OR subtopic LIKE '%' || :query || '%')
          AND (:figurOnly = 0 OR hasFigure = 1)
        ORDER BY createdAt DESC
    """)
    suspend fun getFiltered(
        topics: List<String>,
        topicsEmpty: Int,
        difficulties: List<String>,
        difficultiesEmpty: Int,
        query: String,
        figurOnly: Int
    ): List<Question>

    @Query("SELECT DISTINCT topic FROM questions ORDER BY topic")
    suspend fun getAllTopics(): List<String>

    @Query("SELECT DISTINCT subtopic FROM questions WHERE topic = :topic ORDER BY subtopic")
    suspend fun getSubtopics(topic: String): List<String>

    @Query("SELECT COUNT(*) FROM questions")
    suspend fun getTotalCount(): Int

    @Query("SELECT COUNT(*) FROM questions WHERE difficulty = :difficulty")
    suspend fun getCountByDifficulty(difficulty: String): Int

    @Query("""
        SELECT topic, subtopic, 
               COUNT(*) as totalCount,
               SUM(CASE WHEN difficulty = 'EASY' THEN 1 ELSE 0 END) as easyCount,
               SUM(CASE WHEN difficulty = 'MEDIUM' THEN 1 ELSE 0 END) as mediumCount,
               SUM(CASE WHEN difficulty = 'HARD' THEN 1 ELSE 0 END) as hardCount
        FROM questions 
        GROUP BY topic, subtopic
        ORDER BY topic, subtopic
    """)
    suspend fun getTopicStats(): List<TopicStatsRaw>

    @Query("""
        SELECT * FROM questions
        WHERE (:topicsEmpty = 1 OR topic IN (:topics))
          AND (:difficultiesEmpty = 1 OR difficulty IN (:difficulties))
        ORDER BY RANDOM()
        LIMIT :limit
    """)
    suspend fun getRandomQuestions(
        topics: List<String>,
        topicsEmpty: Int,
        difficulties: List<String>,
        difficultiesEmpty: Int,
        limit: Int
    ): List<Question>

    @Query("SELECT * FROM questions WHERE id IN (:ids)")
    suspend fun getByIds(ids: List<String>): List<Question>
}

data class TopicStatsRaw(
    val topic: String,
    val subtopic: String,
    val totalCount: Int,
    val easyCount: Int,
    val mediumCount: Int,
    val hardCount: Int
)

// ──────────────────────────────────────────────
// TEST DAO
// ──────────────────────────────────────────────
@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(test: Test)

    @Update
    suspend fun updateTest(test: Test)

    @Delete
    suspend fun deleteTest(test: Test)

    @Query("SELECT * FROM tests ORDER BY createdAt DESC")
    suspend fun getAllTests(): List<Test>

    @Query("SELECT * FROM tests WHERE id = :id")
    suspend fun getTestById(id: String): Test?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswer(answer: TestAnswer)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnswers(answers: List<TestAnswer>)

    @Query("SELECT * FROM test_answers WHERE testId = :testId")
    suspend fun getAnswersForTest(testId: String): List<TestAnswer>

    @Query("DELETE FROM test_answers WHERE testId = :testId")
    suspend fun deleteAnswersForTest(testId: String)
}

// ──────────────────────────────────────────────
// DATABASE
// ──────────────────────────────────────────────
@Database(
    entities = [Question::class, Test::class, TestAnswer::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun questionDao(): QuestionDao
    abstract fun testDao(): TestDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "mathbank.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { INSTANCE = it }
            }
        }
    }
}
