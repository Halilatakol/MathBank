package com.mathbank.ui.activities

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.RadioButton
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mathbank.MathBankApp
import com.mathbank.databinding.ActivitySolveTestBinding
import com.mathbank.data.model.Question
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.ui.viewmodels.SolveTestViewModel
import com.mathbank.ui.viewmodels.SolveTestViewModelFactory

class SolveTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySolveTestBinding
    private var countDownTimer: CountDownTimer? = null

    private val viewModel: SolveTestViewModel by viewModels {
        SolveTestViewModelFactory(QuestionRepository(MathBankApp.instance.database))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySolveTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val testId = intent.getStringExtra("test_id") ?: run { finish(); return }

        setupUI()
        observeViewModel()
        viewModel.loadTest(testId)
    }

    private fun setupUI() {
        binding.btnPrevious.setOnClickListener { viewModel.previousQuestion() }
        binding.btnNext.setOnClickListener { viewModel.nextQuestion() }
        binding.btnFinish.setOnClickListener { showFinishDialog() }

        // Şık seçimi
        listOf(binding.rbA, binding.rbB, binding.rbC, binding.rbD, binding.rbE)
            .forEachIndexed { index, rb ->
                rb.setOnClickListener {
                    viewModel.selectAnswer(index)
                }
            }
    }

    private fun observeViewModel() {
        viewModel.currentQuestion.observe(this) { (question, index, total) ->
            displayQuestion(question, index, total)
        }

        viewModel.selectedAnswer.observe(this) { answerIndex ->
            clearAnswerSelection()
            if (answerIndex >= 0) {
                getRadioButtonAt(answerIndex)?.isChecked = true
            }
        }

        viewModel.timeRemaining.observe(this) { seconds ->
            if (seconds > 0) {
                val min = seconds / 60
                val sec = seconds % 60
                binding.tvTimer.text = String.format("%02d:%02d", min, sec)
                binding.tvTimer.visibility = View.VISIBLE

                if (seconds <= 60) {
                    binding.tvTimer.setTextColor(getColor(android.R.color.holo_red_dark))
                }
            }
        }

        viewModel.testCompleted.observe(this) { result ->
            result?.let { navigateToResult(it.first, it.second) }
        }

        viewModel.timerExpired.observe(this) {
            AlertDialog.Builder(this)
                .setTitle("⏰ Süre Doldu!")
                .setMessage("Test süresi sona erdi.")
                .setPositiveButton("Sonuçları Gör") { _, _ ->
                    viewModel.finishTest()
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun displayQuestion(question: Question, index: Int, total: Int) {
        binding.tvQuestionNumber.text = "Soru ${index + 1} / $total"
        binding.tvQuestionText.text = question.text

        // Konu bilgisi
        binding.tvTopic.text = "${question.topic} › ${question.subtopic}"
        binding.chipDifficulty.text = "${question.difficulty.emoji} ${question.difficulty.displayName}"

        // Görsel
        if (question.imageData != null) {
            val bitmap = BitmapFactory.decodeByteArray(question.imageData, 0, question.imageData.size)
            binding.ivQuestionImage.setImageBitmap(bitmap)
            binding.ivQuestionImage.visibility = View.VISIBLE
        } else {
            binding.ivQuestionImage.visibility = View.GONE
        }

        // Şıklar
        val radioButtons = listOf(binding.rbA, binding.rbB, binding.rbC, binding.rbD, binding.rbE)
        radioButtons.forEach { it.visibility = View.GONE }

        question.options.forEachIndexed { i, option ->
            radioButtons.getOrNull(i)?.apply {
                text = option
                visibility = View.VISIBLE
                isChecked = false
            }
        }

        // Navigasyon butonları
        binding.btnPrevious.isEnabled = index > 0
        binding.btnNext.text = if (index == total - 1) "Bitir" else "Sonraki ›"
        binding.btnNext.setOnClickListener {
            if (index == total - 1) showFinishDialog()
            else viewModel.nextQuestion()
        }

        // Progress
        binding.progressTest.progress = ((index + 1) * 100 / total)

        // Kaçıncı soru cevaplanmış
        binding.tvAnswered.text = "${viewModel.getAnsweredCount()}/$total cevaplanmış"
    }

    private fun clearAnswerSelection() {
        listOf(binding.rbA, binding.rbB, binding.rbC, binding.rbD, binding.rbE)
            .forEach { it.isChecked = false }
    }

    private fun getRadioButtonAt(index: Int): RadioButton? {
        return listOf(binding.rbA, binding.rbB, binding.rbC, binding.rbD, binding.rbE)
            .getOrNull(index)
    }

    private fun showFinishDialog() {
        val answered = viewModel.getAnsweredCount()
        val total = viewModel.totalQuestions

        AlertDialog.Builder(this)
            .setTitle("Testi Bitir")
            .setMessage("$total sorudan $answered tanesini cevapladınız.\n\nTesti bitirmek istiyor musunuz?")
            .setPositiveButton("Bitir ve Sonuçları Gör") { _, _ ->
                viewModel.finishTest()
            }
            .setNegativeButton("Devam Et", null)
            .show()
    }

    private fun navigateToResult(testId: String, score: Int) {
        startActivity(Intent(this, TestResultActivity::class.java).apply {
            putExtra("test_id", testId)
            putExtra("score", score)
        })
        finish()
    }

    override fun onBackPressed() {
        showFinishDialog()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
