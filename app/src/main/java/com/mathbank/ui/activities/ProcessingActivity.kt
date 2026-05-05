package com.mathbank.ui.activities

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.mathbank.MathBankApp
import com.mathbank.databinding.ActivityProcessingBinding
import com.mathbank.data.model.ProcessingStatus
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.data.repository.SettingsManager
import com.mathbank.ui.viewmodels.ProcessingViewModel
import com.mathbank.ui.viewmodels.ProcessingViewModelFactory

class ProcessingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProcessingBinding

    private val viewModel: ProcessingViewModel by viewModels {
        ProcessingViewModelFactory(
            QuestionRepository(MathBankApp.instance.database),
            SettingsManager(this),
            application
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProcessingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uriString = intent.getStringExtra("pdf_uri") ?: run { finish(); return }
        val pdfName = intent.getStringExtra("pdf_name") ?: "PDF"

        setupUI(pdfName)
        observeViewModel()
        viewModel.startProcessing(Uri.parse(uriString), pdfName)
    }

    private fun setupUI(pdfName: String) {
        binding.tvPdfName.text = pdfName
        binding.btnCancel.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Islemi Iptal Et")
                .setMessage("PDF islemi durdurulsun mu? Islenen sorular kaydedilecek.")
                .setPositiveButton("Iptal Et") { _, _ ->
                    viewModel.cancelProcessing()
                    finish()
                }
                .setNegativeButton("Devam Et", null)
                .show()
        }
        binding.btnDone.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.progress.observe(this) { progress ->
            binding.progressBar.progress = if (progress.totalPages > 0)
                (progress.currentPage * 100 / progress.totalPages) else 0

            binding.tvPageInfo.text = "Sayfa ${progress.currentPage} / ${progress.totalPages}"
            binding.tvQuestionsFound.text = "${progress.questionsFound} soru bulundu"

            val statusText = when (progress.status) {
                ProcessingStatus.PREPARING -> "Hazirlaniyor..."
                ProcessingStatus.PROCESSING -> "Sayfa analiz ediliyor..."
                ProcessingStatus.SAVING -> "Kaydediliyor..."
                ProcessingStatus.COMPLETED -> "Tamamlandi!"
                ProcessingStatus.ERROR -> "Hata olustu"
            }
            binding.tvStatus.text = statusText

            when (progress.status) {
                ProcessingStatus.COMPLETED -> {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnDone.visibility = View.VISIBLE
                    binding.progressBar.progress = 100
                    binding.lottieSuccess.visibility = View.VISIBLE
                }
                ProcessingStatus.ERROR -> {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnDone.visibility = View.VISIBLE
                }
                else -> {}
            }
        }

        viewModel.currentPageLog.observe(this) { logs ->
            binding.tvLog.text = logs.takeLast(8).joinToString("\n")
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error
            }
        }
    }

    override fun onBackPressed() {
        if (viewModel.isProcessing()) {
            binding.btnCancel.performClick()
        } else {
            super.onBackPressed()
        }
    }
}
