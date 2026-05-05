package com.mathbank.ui.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.mathbank.databinding.ActivityMainBinding
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.data.repository.SettingsManager
import com.mathbank.MathBankApp
import com.mathbank.ui.viewmodels.MainViewModel
import com.mathbank.ui.viewmodels.MainViewModelFactory
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var settingsManager: SettingsManager

    private val viewModel: MainViewModel by viewModels {
        MainViewModelFactory(
            QuestionRepository(MathBankApp.instance.database),
            SettingsManager(this)
        )
    }

    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { startProcessing(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsManager = SettingsManager(this)
        setupUI()
        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadStats()
    }

    private fun setupUI() {
        // PDF Seç butonu
        binding.btnSelectPdf.setOnClickListener {
            lifecycleScope.launch {
                if (!settingsManager.isApiKeySet()) {
                    showApiKeyDialog()
                } else {
                    pdfPickerLauncher.launch("application/pdf")
                }
            }
        }

        // Soru Bankası butonu
        binding.btnQuestionBank.setOnClickListener {
            startActivity(Intent(this, QuestionBankActivity::class.java))
        }

        // Test Oluştur butonu
        binding.btnCreateTest.setOnClickListener {
            lifecycleScope.launch {
                val count = viewModel.totalQuestions.value ?: 0
                if (count == 0) {
                    Toast.makeText(this@MainActivity,
                        "Önce PDF ekleyerek soru bankasını doldurun", Toast.LENGTH_SHORT).show()
                } else {
                    startActivity(Intent(this@MainActivity, CreateTestActivity::class.java))
                }
            }
        }

        // Ayarlar butonu
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        // Testlerim butonu
        binding.btnMyTests.setOnClickListener {
            startActivity(Intent(this, QuestionBankActivity::class.java).apply {
                putExtra("tab", "tests")
            })
        }
    }

    private fun observeViewModel() {
        viewModel.totalQuestions.observe(this) { count ->
            binding.tvTotalQuestions.text = "$count Soru"
        }

        viewModel.easyCount.observe(this) { count ->
            binding.tvEasyCount.text = count.toString()
        }

        viewModel.mediumCount.observe(this) { count ->
            binding.tvMediumCount.text = count.toString()
        }

        viewModel.hardCount.observe(this) { count ->
            binding.tvHardCount.text = count.toString()
        }

        viewModel.totalTests.observe(this) { count ->
            binding.tvTotalTests.text = "$count Test"
        }
    }

    private fun startProcessing(uri: Uri) {
        val intent = Intent(this, ProcessingActivity::class.java).apply {
            putExtra("pdf_uri", uri.toString())
            putExtra("pdf_name", getFileName(uri))
        }
        startActivity(intent)
    }

    private fun getFileName(uri: Uri): String {
        return contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            cursor.moveToFirst()
            cursor.getString(nameIndex)
        } ?: "unknown.pdf"
    }

    private fun showApiKeyDialog() {
        val dialog = ApiKeyDialogFragment()
        dialog.onKeySaved = {
            pdfPickerLauncher.launch("application/pdf")
        }
        dialog.show(supportFragmentManager, "api_key")
    }
}
