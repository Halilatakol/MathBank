package com.mathbank.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.chip.Chip
import com.mathbank.MathBankApp
import com.mathbank.R
import com.mathbank.databinding.ActivityCreateTestBinding
import com.mathbank.data.model.Difficulty
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.ui.viewmodels.CreateTestViewModel
import com.mathbank.ui.viewmodels.CreateTestViewModelFactory

class CreateTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateTestBinding

    private val viewModel: CreateTestViewModel by viewModels {
        CreateTestViewModelFactory(QuestionRepository(MathBankApp.instance.database))
    }

    private val selectedTopics = mutableSetOf<String>()
    private val selectedDifficulties = mutableSetOf<Difficulty>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeViewModel()
        viewModel.loadTopics()
    }

    private fun setupUI() {
        // Soru sayısı slider
        binding.sliderQuestionCount.addOnChangeListener { _, value, _ ->
            binding.tvQuestionCount.text = "${value.toInt()} soru"
        }
        binding.sliderQuestionCount.value = 20f

        // Süre slider
        binding.sliderTimeLimit.addOnChangeListener { _, value, _ ->
            binding.tvTimeLimit.text = if (value == 0f) "Süresiz" else "${value.toInt()} dakika"
        }

        // Zorluk chip'leri
        setupDifficultyChips()

        // Tüm zorluklar toggle
        binding.chipAllDifficulties.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                selectedDifficulties.clear()
                updateAvailableCount()
            }
        }

        // Karıştır switch
        binding.switchShuffle.isChecked = true

        // Test oluştur butonu
        binding.btnCreateTest.setOnClickListener {
            createTest()
        }
    }

    private fun setupDifficultyChips() {
        listOf(
            binding.chipEasy to Difficulty.EASY,
            binding.chipMedium to Difficulty.MEDIUM,
            binding.chipHard to Difficulty.HARD
        ).forEach { (chip, difficulty) ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedDifficulties.add(difficulty)
                else selectedDifficulties.remove(difficulty)

                // Tüm zorluklar chip'ini güncelle
                binding.chipAllDifficulties.isChecked = selectedDifficulties.isEmpty()
                updateAvailableCount()
            }
        }
    }

    private fun observeViewModel() {
        viewModel.topics.observe(this) { topics ->
            binding.chipGroupTopics.removeAllViews()

            // "Tümü" chip'i
            val allChip = Chip(this).apply {
                text = "Tümü"
                isCheckable = true
                isChecked = true
                id = 9999
            }
            allChip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedTopics.clear()
                    updateAvailableCount()
                }
            }
            binding.chipGroupTopics.addView(allChip)

            topics.forEach { topic ->
                val chip = Chip(this).apply {
                    text = topic
                    isCheckable = true
                }
                chip.setOnCheckedChangeListener { _, isChecked ->
                    allChip.isChecked = false
                    if (isChecked) selectedTopics.add(topic)
                    else selectedTopics.remove(topic)
                    if (selectedTopics.isEmpty()) allChip.isChecked = true
                    updateAvailableCount()
                }
                binding.chipGroupTopics.addView(chip)
            }
        }

        viewModel.availableCount.observe(this) { count ->
            binding.tvAvailable.text = "Uygun soru: $count"
            val requested = binding.sliderQuestionCount.value.toInt()
            binding.btnCreateTest.isEnabled = count > 0
            if (count < requested && count > 0) {
                binding.sliderQuestionCount.value = count.toFloat()
            }
        }
    }

    private fun updateAvailableCount() {
        viewModel.getAvailableCount(
            topics = selectedTopics.toList(),
            difficulties = selectedDifficulties.toList()
        )
    }

    private fun createTest() {
        val questionCount = binding.sliderQuestionCount.value.toInt()
        val timeLimit = binding.sliderTimeLimit.value.toInt()
        val shuffle = binding.switchShuffle.isChecked
        val testName = binding.etTestName.text.toString().let {
            if (it.isBlank()) generateTestName() else it
        }

        viewModel.createTest(
            name = testName,
            topics = selectedTopics.toList(),
            difficulties = selectedDifficulties.toList(),
            questionCount = questionCount,
            timeLimit = timeLimit,
            shuffle = shuffle
        ) { testId ->
            startActivity(Intent(this, SolveTestActivity::class.java).apply {
                putExtra("test_id", testId)
            })
            finish()
        }
    }

    private fun generateTestName(): String {
        val topics = if (selectedTopics.isEmpty()) "Karma" else selectedTopics.take(2).joinToString("+")
        val difficulty = when {
            selectedDifficulties.isEmpty() -> "Mix"
            selectedDifficulties.size == 1 -> selectedDifficulties.first().displayName
            else -> "Mix"
        }
        return "$topics - $difficulty Test"
    }
}
