package com.mathbank.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.mathbank.ai.ClaudeService
import com.mathbank.data.model.ProcessingProgress
import com.mathbank.data.model.ProcessingStatus
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.data.repository.SettingsManager
import com.mathbank.pdf.PdfProcessor
import kotlinx.coroutines.*

class ProcessingViewModel(
    private val repository: QuestionRepository,
    private val settingsManager: SettingsManager,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ProcessingViewModel"
        // Sayfalar arasi bekleme - rate limit icin
        private const val API_DELAY_MS = 4000L
    }

    private val _progress = MutableLiveData<ProcessingProgress>()
    val progress: LiveData<ProcessingProgress> = _progress

    private val _currentPageLog = MutableLiveData<MutableList<String>>(mutableListOf())
    val currentPageLog: LiveData<MutableList<String>> = _currentPageLog

    private val _error = MutableLiveData<String>("")
    val error: LiveData<String> = _error

    private var processingJob: Job? = null
    private var totalQuestionsFound = 0
    private val pdfProcessor = PdfProcessor(application)

    fun startProcessing(uri: Uri, pdfName: String) {
        processingJob = viewModelScope.launch {
            try {
                val apiKey = settingsManager.getApiKey()
                if (apiKey.isEmpty()) {
                    _error.postValue("API anahtari bulunamadi. Lutfen ayarlardan ekleyin.")
                    return@launch
                }

                val claude = ClaudeService(apiKey)
                totalQuestionsFound = 0

                updateProgress(0, 0, ProcessingStatus.PREPARING)
                val totalPages = pdfProcessor.getPageCount(uri)
                addLog("PDF acildi: $pdfName ($totalPages sayfa)")

                if (totalPages == 0) {
                    _error.postValue("PDF okunamadi veya bos.")
                    return@launch
                }

                updateProgress(0, totalPages, ProcessingStatus.PROCESSING)

                for (pageIndex in 0 until totalPages) {
                    if (!isActive) {
                        addLog("Islem iptal edildi")
                        break
                    }

                    val pageNumber = pageIndex + 1
                    addLog("Sayfa $pageNumber/$totalPages analiz ediliyor...")
                    updateProgress(pageIndex, totalPages, ProcessingStatus.PROCESSING)

                    val pageResult = pdfProcessor.renderPage(uri, pageIndex)
                    if (pageResult == null) {
                        addLog("  Sayfa $pageNumber render edilemedi, atlaniyor")
                        continue
                    }

                    val result = claude.analyzePage(pageResult.bitmap, pageNumber, pdfName)

                    result.onSuccess { analysis ->
                        val questions = analysis.questions
                        addLog("  ${questions.size} soru bulundu")

                        updateProgress(pageIndex, totalPages, ProcessingStatus.SAVING)
                        for (extracted in questions) {
                            if (!isActive) break

                            val croppedBitmap = extracted.boundingBox?.let { bbox ->
                                claude.cropQuestionImage(pageResult.bitmap, bbox)
                            }

                            repository.saveQuestion(
                                extracted = extracted,
                                pageBitmap = pageResult.bitmap,
                                croppedBitmap = croppedBitmap,
                                sourcePdf = pdfName,
                                pageNumber = pageNumber
                            )
                            totalQuestionsFound++
                        }

                        updateProgress(pageIndex + 1, totalPages, ProcessingStatus.PROCESSING)

                    }.onFailure { error ->
                        addLog("  Sayfa $pageNumber hatasi: ${error.message}")
                        Log.e(TAG, "Page $pageNumber error", error)
                    }

                    pageResult.bitmap.recycle()

                    if (pageIndex < totalPages - 1 && isActive) {
                        delay(API_DELAY_MS)
                    }
                }

                if (isActive) {
                    addLog("Tamamlandi! Toplam $totalQuestionsFound soru eklendi.")
                    updateProgress(totalPages, totalPages, ProcessingStatus.COMPLETED)
                }

            } catch (e: CancellationException) {
                addLog("Islem iptal edildi. $totalQuestionsFound soru kaydedildi.")
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Processing error", e)
                _error.postValue(e.message ?: "Bilinmeyen hata")
                updateProgress(0, 0, ProcessingStatus.ERROR)
            }
        }
    }

    fun cancelProcessing() {
        processingJob?.cancel()
    }

    fun isProcessing(): Boolean = processingJob?.isActive == true

    private fun updateProgress(current: Int, total: Int, status: ProcessingStatus) {
        _progress.postValue(
            ProcessingProgress(
                currentPage = current,
                totalPages = total,
                questionsFound = totalQuestionsFound,
                status = status
            )
        )
    }

    private fun addLog(message: String) {
        val currentLogs = _currentPageLog.value ?: mutableListOf()
        currentLogs.add(message)
        _currentPageLog.postValue(currentLogs)
        Log.d(TAG, message)
    }

    override fun onCleared() {
        super.onCleared()
        processingJob?.cancel()
    }
}

class ProcessingViewModelFactory(
    private val repository: QuestionRepository,
    private val settings: SettingsManager,
    private val app: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ProcessingViewModel(repository, settings, app) as T
    }
}
