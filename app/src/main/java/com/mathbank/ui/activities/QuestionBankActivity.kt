package com.mathbank.ui.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.chip.Chip
import com.mathbank.MathBankApp
import com.mathbank.databinding.ActivityQuestionBankBinding
import com.mathbank.data.model.Difficulty
import com.mathbank.data.model.Question
import com.mathbank.data.model.QuestionFilter
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.ui.adapters.QuestionAdapter
import com.mathbank.ui.adapters.TestAdapter
import com.mathbank.ui.viewmodels.QuestionBankViewModel
import com.mathbank.ui.viewmodels.QuestionBankViewModelFactory

class QuestionBankActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuestionBankBinding
    private lateinit var questionAdapter: QuestionAdapter
    private lateinit var testAdapter: TestAdapter

    private val viewModel: QuestionBankViewModel by viewModels {
        QuestionBankViewModelFactory(QuestionRepository(MathBankApp.instance.database))
    }

    private val selectedTopics = mutableSetOf<String>()
    private val selectedDifficulties = mutableSetOf<Difficulty>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuestionBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerViews()
        setupTabs()
        setupSearch()
        setupFilters()
        observeViewModel()

        // Hangi tab açılacak?
        val startTab = intent.getStringExtra("tab")
        if (startTab == "tests") {
            binding.tabLayout.getTabAt(1)?.select()
        }
    }

    private fun setupRecyclerViews() {
        // Sorular
        questionAdapter = QuestionAdapter(
            onQuestionClick = { question -> openQuestionDetail(question) },
            onDeleteClick = { question -> viewModel.deleteQuestion(question) }
        )
        binding.rvQuestions.apply {
            layoutManager = LinearLayoutManager(this@QuestionBankActivity)
            adapter = questionAdapter
        }

        // Testler
        testAdapter = TestAdapter(
            onTestClick = { test ->
                startActivity(Intent(this, SolveTestActivity::class.java).apply {
                    putExtra("test_id", test.id)
                })
            },
            onDeleteClick = { test -> viewModel.deleteTest(test) }
        )
        binding.rvTests.apply {
            layoutManager = LinearLayoutManager(this@QuestionBankActivity)
            adapter = testAdapter
        }
    }

    private fun setupTabs() {
        binding.tabLayout.addOnTabSelectedListener(object : com.google.android.material.tabs.TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: com.google.android.material.tabs.TabLayout.Tab?) {
                when (tab?.position) {
                    0 -> {
                        binding.questionContainer.visibility = View.VISIBLE
                        binding.testContainer.visibility = View.GONE
                    }
                    1 -> {
                        binding.questionContainer.visibility = View.GONE
                        binding.testContainer.visibility = View.VISIBLE
                    }
                }
            }
            override fun onTabUnselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
            override fun onTabReselected(tab: com.google.android.material.tabs.TabLayout.Tab?) {}
        })
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                applyFilter()
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    private fun setupFilters() {
        // Zorluk chip'leri
        mapOf(
            binding.chipEasy to Difficulty.EASY,
            binding.chipMedium to Difficulty.MEDIUM,
            binding.chipHard to Difficulty.HARD
        ).forEach { (chip, difficulty) ->
            chip.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) selectedDifficulties.add(difficulty)
                else selectedDifficulties.remove(difficulty)
                applyFilter()
            }
        }

        binding.chipGroupTopics.setOnCheckedStateChangeListener { group, checkedIds ->
            selectedTopics.clear()
            checkedIds.forEach { id ->
                val chip = group.findViewById<Chip>(id)
                chip?.text?.toString()?.let { selectedTopics.add(it) }
            }
            applyFilter()
        }

        binding.btnCreateTest.setOnClickListener {
            startActivity(Intent(this, CreateTestActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.questions.observe(this) { questions ->
            questionAdapter.submitList(questions)
            binding.tvQuestionCount.text = "${questions.size} soru"
            binding.emptyViewQuestions.visibility = if (questions.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.tests.observe(this) { tests ->
            testAdapter.submitList(tests)
            binding.tvTestCount.text = "${tests.size} test"
            binding.emptyViewTests.visibility = if (tests.isEmpty()) View.VISIBLE else View.GONE
        }

        viewModel.topics.observe(this) { topics ->
            binding.chipGroupTopics.removeAllViews()
            topics.forEach { topic ->
                val chip = Chip(this).apply {
                    text = topic
                    isCheckable = true
                    isClickable = true
                }
                binding.chipGroupTopics.addView(chip)
            }
        }
    }

    private fun applyFilter() {
        val filter = QuestionFilter(
            topics = selectedTopics.toList(),
            difficulties = selectedDifficulties.toList(),
            searchQuery = binding.etSearch.text.toString()
        )
        viewModel.applyFilter(filter)
    }

    private fun openQuestionDetail(question: Question) {
        startActivity(Intent(this, QuestionDetailActivity::class.java).apply {
            putExtra("question_id", question.id)
        })
    }
}
