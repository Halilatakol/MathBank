package com.mathbank.ui.viewmodels

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.*
import com.mathbank.ai.ClaudeService
import com.mathbank.data.model.ExtractedQuestion
import com.mathbank.data.model.ProcessingProgress
import com.mathbank.data.model.ProcessingStatus
import com.mathbank.data.repository.QuestionRepository
import com.mathbank.data.repository.SettingsManager
import com.mathbank.pdf.PdfProcessor
import com.mathbank.pdf.QuestionCropper
import kotlinx.coroutines.*

class ProcessingViewModel(
    private val repository: QuestionRepository,
    private val settingsManager: SettingsManager,
    application: Application
) : AndroidViewModel(application) {

    companion object {
        private const val TAG = "ProcessingViewModel"
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
    private val questionCropper = QuestionCropper()

    fun startProcessing(uri: Uri, pdfName: String) {
        processingJob = viewModelScope.launch {
            try {
                val apiKey = settingsManager.getActiveApiKey()
                val selectedModel = settingsManager.getSelectedModel()

                if (apiKey.isEmpty()) {
                    _error.postValue("API anahtari bulunamadi. Ayarlardan ekleyin.")
                    updateProgress(0, 0, ProcessingStatus.ERROR)
                    return@launch
                }

                val modelName = if (selectedModel == SettingsManager.MODEL_GEMINI) "Gemini" else "OpenRouter"
                val aiService = ClaudeService(apiKey, selectedModel)
                totalQuestionsFound = 0

                updateProgress(0, 0, ProcessingStatus.PREPARING)
                val totalPages = pdfProcessor.getPageCount(uri)
                addLog("PDF acildi: $pdfName ($totalPages sayfa)")
                addLog("Model: $modelName")

                if (totalPages == 0) {
                    _error.postValue("PDF okunamadi veya bos.")
                    updateProgress(0, 0, ProcessingStatus.ERROR)
                    return@launch
                }

                updateProgress(0, totalPages, ProcessingStatus.PROCESSING)

                for (pageIndex in 0 until totalPages) {
                    if (!isActive) { addLog("Islem iptal edildi"); break }

                    val pageNumber = pageIndex + 1
                    addLog("Sayfa $pageNumber/$totalPages analiz ediliyor...")
                    updateProgress(pageIndex, totalPages, ProcessingStatus.PROCESSING)

                    val pageResult = pdfProcessor.renderPage(uri, pageIndex)
                    if (pageResult == null) {
                        addLog("  Sayfa $pageNumber render edilemedi")
                        continue
                    }

                    // OCR ile soru bolgelerini tespit et
                    val questionRegions = try {
                        questionCropper.cropQuestions(pageResult.bitmap)
                    } catch (e: Exception) {
                        addLog("  OCR hatasi: ${e.message}")
                        emptyList()
                    }

                    if (questionRegions.isNotEmpty()) {
                        // OCR BASARILI - her bolgeyi ayri kaydet
                        addLog("  OCR: ${questionRegions.size} soru bolgesi bulundu")
                        updateProgress(pageIndex, totalPages, ProcessingStatus.SAVING)

                        for (region in questionRegions) {
                            if (!isActive) break

                            // Kirpilmis bölgeyi AI ile analiz et
                            val result = aiService.analyzePage(region.bitmap, pageNumber, pdfName)

                            result.onSuccess { analysis ->
                                val extracted = if (analysis.questions.isNotEmpty()) {
                                    analysis.questions.first().copy(
                                        questionNumber = region.questionNumber
                                    )
                                } else {
                                    ExtractedQuestion(
                                        questionNumber = region.questionNumber,
                                        text = "Soru ${region.questionNumber}",
                                        topic = "Genel Matematik",
                                        subtopic = "",
                                        difficulty = "MEDIUM",
                                        options = emptyList(),
                                        correctAnswer = null,
                                        hasFigure = true,
                                        questionType = "MULTIPLE_CHOICE",
                                        boundingBox = null
                                    )
                                }
                                // OCR'in kirptigi bitmap kaydediliyor
                                repository.saveQuestion(
                                    extracted = extracted,
                                    pageBitmap = pageResult.bitmap,
                                    croppedBitmap = region.bitmap,
                                    sourcePdf = pdfName,
                                    pageNumber = pageNumber
                                )
                                totalQuestionsFound++
                                addLog("    Soru ${region.questionNumber} kaydedildi")

                            }.onFailure {
                                // AI hata verse bile OCR gorselini kaydet
                                repository.saveQuestion(
                                    extracted = ExtractedQuestion(
                                        questionNumber = region.questionNumber,
                                        text = "Soru ${region.questionNumber}",
                                        topic = "Genel Matematik",
                                        subtopic = "",
                                        difficulty = "MEDIUM",
                                        options = emptyList(),
                                        correctAnswer = null,
                                        hasFigure = true,
                                        questionType = "MULTIPLE_CHOICE",
                                        boundingBox = null
                                    ),
                                    pageBitmap = pageResult.bitmap,
                                    croppedBitmap = region.bitmap,
                                    sourcePdf = pdfName,
                                    pageNumber = pageNumber
                                )
                                totalQuestionsFound++
                                addLog("    Soru ${region.questionNumber} gorsel kaydedildi")
                            }

                            if (isActive) delay(API_DELAY_MS)
                        }

                    } else {
                        // OCR BULAMADI - AI ile analiz
                        addLog("  OCR soru bulamadi, AI analizi yapiliyor...")
                        val result = aiService.analyzePage(pageResult.bitmap, pageNumber, pdfName)

                        result.onSuccess { analysis ->
                            addLog("  AI: ${analysis.questions.size} soru bulundu")
                            updateProgress(pageIndex, totalPages, ProcessingStatus.SAVING)
                            for (extracted in analysis.questions) {
                                if (!isActive) break
                                val croppedBitmap = extracted.boundingBox?.let { bbox ->
                                    aiService.cropQuestionImage(pageResult.bitmap, bbox)
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
                        }.onFailure { err ->
                            addLog("  Sayfa $pageNumber hatasi: ${err.message}")
                        }
                    }

                    pageResult.bitmap.recycle()
                    updateProgress(pageIndex + 1, totalPages, ProcessingStatus.PROCESSING)
                    if (pageIndex < totalPages - 1 && isActive) delay(2000L)
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

    fun cancelProcessing() { processingJob?.cancel() }
    fun isProcessing(): Boolean = processingJob?.isActive == true

    private fun updateProgress(current: Int, total: Int, status: ProcessingStatus) {
        _progress.postValue(ProcessingProgress(
            currentPage = current,
            totalPages = total,
            questionsFound = totalQuestionsFound,
            status = status
        ))
    }

    private fun addLog(message: String) {
        val logs = _currentPageLog.value ?: mutableListOf()
        logs.add(message)
        _currentPageLog.postValue(logs)
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
