package com.mathbank.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mathbank.MathBankApp
import com.mathbank.R
import com.mathbank.databinding.ActivityTestResultBinding
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.ui.viewmodels.TestResultViewModel
import com.mathbank.ui.viewmodels.TestResultViewModelFactory

class TestResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTestResultBinding

    private val viewModel: TestResultViewModel by viewModels {
        TestResultViewModelFactory(QuestionRepository(MathBankApp.instance.database))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTestResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testId = intent.getStringExtra("test_id") ?: run { finish(); return }
        val score = intent.getIntExtra("score", 0)

        setupUI(score)
        observeViewModel()
        viewModel.loadResults(testId)
    }

    private fun setupUI(score: Int) {
        binding.tvScore.text = "%$score"
        binding.progressScore.progress = score

        val message = when {
            score >= 90 -> "🏆 Mükemmel!"
            score >= 70 -> "👏 Çok İyi!"
            score >= 50 -> "👍 İyi"
            else -> "📚 Daha çok çalış"
        }
        binding.tvMessage.text = message

        binding.btnReturnHome.setOnClickListener { finish() }
        binding.btnReviewAnswers.setOnClickListener {
            binding.btnReviewAnswers.visibility = View.GONE
            binding.rvAnswers.visibility = View.VISIBLE
        }
    }

    private fun observeViewModel() {
        viewModel.results.observe(this) { results ->
            binding.tvCorrect.text = "✅ ${results.count { it.isCorrect }} Doğru"
            binding.tvWrong.text = "❌ ${results.count { !it.isCorrect && it.selectedAnswer != null }} Yanlış"
            binding.tvEmpty.text = "⬜ ${results.count { it.selectedAnswer == null }} Boş"

            // Konu bazlı analiz
            val topicScores = results.groupBy { it.question.topic }
            val topicSummary = topicScores.map { (topic, items) ->
                val correct = items.count { it.isCorrect }
                "$topic: $correct/${items.size}"
            }.joinToString("\n")
            binding.tvTopicBreakdown.text = topicSummary
        }
    }

    data class AnswerResult(
        val question: com.mathbank.data.model.Question,
        val selectedAnswer: String?,
        val isCorrect: Boolean
    )
}
