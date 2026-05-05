package com.mathbank.ui.viewmodels

import androidx.lifecycle.*
import com.mathbank.data.model.*
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.data.repository.SettingsManager
import kotlinx.coroutines.launch

// ──────────────────────────────────────────────
// MAIN VIEW MODEL
// ──────────────────────────────────────────────
class MainViewModel(
    private val repository: QuestionRepository,
    private val settings: SettingsManager
) : ViewModel() {

    private val _totalQuestions = MutableLiveData<Int>(0)
    val totalQuestions: LiveData<Int> = _totalQuestions

    private val _easyCount = MutableLiveData<Int>(0)
    val easyCount: LiveData<Int> = _easyCount

    private val _mediumCount = MutableLiveData<Int>(0)
    val mediumCount: LiveData<Int> = _mediumCount

    private val _hardCount = MutableLiveData<Int>(0)
    val hardCount: LiveData<Int> = _hardCount

    private val _totalTests = MutableLiveData<Int>(0)
    val totalTests: LiveData<Int> = _totalTests

    fun loadStats() {
        viewModelScope.launch {
            _totalQuestions.postValue(repository.getTotalCount())
            _easyCount.postValue(repository.getCountByDifficulty(Difficulty.EASY))
            _mediumCount.postValue(repository.getCountByDifficulty(Difficulty.MEDIUM))
            _hardCount.postValue(repository.getCountByDifficulty(Difficulty.HARD))
            _totalTests.postValue(repository.getAllTests().size)
        }
    }
}

class MainViewModelFactory(
    private val repository: QuestionRepository,
    private val settings: SettingsManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return MainViewModel(repository, settings) as T
    }
}

// ──────────────────────────────────────────────
// QUESTION BANK VIEW MODEL
// ──────────────────────────────────────────────
class QuestionBankViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _questions = MutableLiveData<List<Question>>()
    val questions: LiveData<List<Question>> = _questions

    private val _topics = MutableLiveData<List<String>>()
    val topics: LiveData<List<String>> = _topics

    private val _tests = MutableLiveData<List<Test>>()
    val tests: LiveData<List<Test>> = _tests

    init {
        loadAll()
    }

    fun loadAll() {
        viewModelScope.launch {
            _questions.postValue(repository.getAllQuestions())
            _topics.postValue(repository.getAllTopics())
            _tests.postValue(repository.getAllTests())
        }
    }

    fun applyFilter(filter: QuestionFilter) {
        viewModelScope.launch {
            _questions.postValue(repository.getFilteredQuestions(filter))
        }
    }

    fun deleteQuestion(question: Question) {
        viewModelScope.launch {
            repository.deleteQuestion(question)
            loadAll()
        }
    }

    fun deleteTest(test: Test) {
        viewModelScope.launch {
            repository.deleteTest(test)
            _tests.postValue(repository.getAllTests())
        }
    }
}

class QuestionBankViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuestionBankViewModel(repository) as T
    }
}

// ──────────────────────────────────────────────
// CREATE TEST VIEW MODEL
// ──────────────────────────────────────────────
class CreateTestViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private val _topics = MutableLiveData<List<String>>()
    val topics: LiveData<List<String>> = _topics

    private val _availableCount = MutableLiveData<Int>(0)
    val availableCount: LiveData<Int> = _availableCount

    fun loadTopics() {
        viewModelScope.launch {
            _topics.postValue(repository.getAllTopics())
        }
    }

    fun getAvailableCount(topics: List<String>, difficulties: List<Difficulty>) {
        viewModelScope.launch {
            val questions = repository.getRandomQuestions(
                topics = topics,
                difficulties = difficulties,
                count = 1000
            )
            _availableCount.postValue(questions.size)
        }
    }

    fun createTest(
        name: String,
        topics: List<String>,
        difficulties: List<Difficulty>,
        questionCount: Int,
        timeLimit: Int,
        shuffle: Boolean,
        onCreated: (String) -> Unit
    ) {
        viewModelScope.launch {
            val questions = repository.getRandomQuestions(
                topics = topics,
                difficulties = difficulties,
                count = questionCount
            )

            val finalQuestions = if (shuffle) questions.shuffled() else questions
            val questionIds = finalQuestions.map { it.id }

            val test = repository.createTest(
                name = name,
                questionIds = questionIds,
                topics = topics,
                difficulties = difficulties,
                totalTime = timeLimit
            )

            onCreated(test.id)
        }
    }
}

class CreateTestViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return CreateTestViewModel(repository) as T
    }
}

// ──────────────────────────────────────────────
// SOLVE TEST VIEW MODEL
// ──────────────────────────────────────────────
class SolveTestViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    private var test: Test? = null
    private var questions: List<Question> = emptyList()
    private val answers = mutableMapOf<String, Int>()  // questionId -> optionIndex
    private var currentIndex = 0

    private val _currentQuestion = MutableLiveData<Triple<Question, Int, Int>>()
    val currentQuestion: LiveData<Triple<Question, Int, Int>> = _currentQuestion

    private val _selectedAnswer = MutableLiveData<Int>(-1)
    val selectedAnswer: LiveData<Int> = _selectedAnswer

    private val _timeRemaining = MutableLiveData<Int>(0)
    val timeRemaining: LiveData<Int> = _timeRemaining

    private val _testCompleted = MutableLiveData<Pair<String, Int>?>()
    val testCompleted: LiveData<Pair<String, Int>?> = _testCompleted

    private val _timerExpired = MutableLiveData<Unit>()
    val timerExpired: LiveData<Unit> = _timerExpired

    val totalQuestions: Int get() = questions.size

    fun loadTest(testId: String) {
        viewModelScope.launch {
            val t = repository.getTestById(testId) ?: return@launch
            test = t
            questions = repository.getQuestionsByIds(t.questionIds)
            showCurrentQuestion()

            if (t.totalTime > 0) {
                startTimer(t.totalTime * 60)
            }
        }
    }

    private fun showCurrentQuestion() {
        val q = questions.getOrNull(currentIndex) ?: return
        _currentQuestion.postValue(Triple(q, currentIndex, questions.size))
        _selectedAnswer.postValue(answers[q.id] ?: -1)
    }

    fun nextQuestion() {
        if (currentIndex < questions.size - 1) {
            currentIndex++
            showCurrentQuestion()
        }
    }

    fun previousQuestion() {
        if (currentIndex > 0) {
            currentIndex--
            showCurrentQuestion()
        }
    }

    fun selectAnswer(optionIndex: Int) {
        val q = questions.getOrNull(currentIndex) ?: return
        answers[q.id] = optionIndex
        _selectedAnswer.postValue(optionIndex)
    }

    fun getAnsweredCount(): Int = answers.size

    fun finishTest() {
        viewModelScope.launch {
            val testId = test?.id ?: return@launch
            var correct = 0

            val testAnswers = questions.map { q ->
                val selectedIndex = answers[q.id]
                val selectedText = selectedIndex?.let { q.options.getOrNull(it) }
                val isCorrect = q.correctAnswer != null &&
                    selectedText?.startsWith(q.correctAnswer!!) == true

                if (isCorrect) correct++

                com.mathbank.data.model.TestAnswer(
                    testId = testId,
                    questionId = q.id,
                    selectedAnswer = selectedText,
                    isCorrect = isCorrect
                )
            }

            val score = if (questions.isNotEmpty())
                (correct * 100 / questions.size) else 0

            repository.saveTestResult(testId, testAnswers, score)
            _testCompleted.postValue(Pair(testId, score))
        }
    }

    private fun startTimer(totalSeconds: Int) {
        var remaining = totalSeconds
        viewModelScope.launch {
            while (remaining > 0) {
                _timeRemaining.postValue(remaining)
                kotlinx.coroutines.delay(1000)
                remaining--
            }
            _timerExpired.postValue(Unit)
        }
    }
}

class SolveTestViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SolveTestViewModel(repository) as T
    }
}

// ──────────────────────────────────────────────
// TEST RESULT VIEW MODEL
// ──────────────────────────────────────────────
class TestResultViewModel(
    private val repository: QuestionRepository
) : ViewModel() {

    data class AnswerResult(
        val question: Question,
        val selectedAnswer: String?,
        val isCorrect: Boolean
    )

    private val _results = MutableLiveData<List<AnswerResult>>()
    val results: LiveData<List<AnswerResult>> = _results

    fun loadResults(testId: String) {
        viewModelScope.launch {
            val answers = repository.getAnswersForTest(testId)
            val questionIds = answers.map { it.questionId }
            val questions = repository.getQuestionsByIds(questionIds).associateBy { it.id }

            val results = answers.mapNotNull { answer ->
                val q = questions[answer.questionId] ?: return@mapNotNull null
                AnswerResult(q, answer.selectedAnswer, answer.isCorrect)
            }
            _results.postValue(results)
        }
    }
}

class TestResultViewModelFactory(
    private val repository: QuestionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return TestResultViewModel(repository) as T
    }
}
