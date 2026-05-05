package com.mathbank.ui.activities

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathbank.MathBankApp
import com.mathbank.databinding.ActivityQuestionDetailBinding
import com.mathbank.data.model.Difficulty
import com.mathbank.data.repository.QuestionRepository
import kotlinx.coroutines.launch

class QuestionDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val questionId = intent.getStringExtra("question_id") ?: run { finish(); return }
        val repository = QuestionRepository(MathBankApp.instance.database)

        lifecycleScope.launch {
            val question = repository.getAllQuestions().firstOrNull { it.id == questionId }
                ?: run { finish(); return@launch }

            // Başlık
            binding.tvTopic.text = question.topic
            binding.tvSubtopic.text = question.subtopic
            binding.tvPage.text = "Sayfa ${question.pageNumber} • ${question.sourcePdf}"

            // Zorluk
            binding.chipDifficulty.text = "${question.difficulty.emoji} ${question.difficulty.displayName}"

            // Görsel
            if (question.imageData != null) {
                val bmp = BitmapFactory.decodeByteArray(question.imageData, 0, question.imageData.size)
                binding.ivQuestionImage.setImageBitmap(bmp)
                binding.ivQuestionImage.visibility = View.VISIBLE
            }

            // Soru metni
            binding.tvQuestionText.text = question.text

            // Şıklar
            if (question.options.isNotEmpty()) {
                binding.tvOptions.text = question.options.joinToString("\n")
                binding.tvOptions.visibility = View.VISIBLE
            }

            // Cevap
            if (question.correctAnswer != null) {
                binding.tvAnswer.text = "✅ Doğru Cevap: ${question.correctAnswer}"
                binding.tvAnswer.visibility = View.VISIBLE
            }
        }

        binding.btnBack.setOnClickListener { finish() }
    }
}
