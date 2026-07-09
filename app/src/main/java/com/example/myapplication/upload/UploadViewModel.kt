package com.example.myapplication.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.PaperStatus
import com.example.myapplication.data.model.UploadSource
import com.example.myapplication.domain.usecase.PollPaperStatusUseCase
import com.example.myapplication.domain.usecase.UploadPaperUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadViewModel(
    private val uploadPaperUseCase: UploadPaperUseCase = UploadPaperUseCase(),
    private val pollPaperStatusUseCase: PollPaperStatusUseCase = PollPaperStatusUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    fun onIntent(intent: UploadIntent) {
        when (intent) {
            is UploadIntent.SelectSource -> _state.update {
                it.copy(source = intent.source, error = null)
            }
            is UploadIntent.SelectPdf -> _state.update {
                it.copy(selectedPdf = intent.uri, selectedPdfName = intent.displayName, error = null)
            }
            is UploadIntent.EnterDoi -> _state.update {
                it.copy(doi = intent.value, error = null)
            }
            UploadIntent.Submit -> submit()
            UploadIntent.Retry -> submit()
        }
    }

    private fun submit() {
        val current = _state.value
        val reference = when (current.source) {
            UploadSource.DOI -> current.doi.trim()
            UploadSource.FILE -> current.selectedPdf?.toString().orEmpty()
        }
        if (reference.isBlank()) {
            val message = when (current.source) {
                UploadSource.DOI -> "Enter a DOI or arXiv URL to continue."
                UploadSource.FILE -> "Pick a PDF file to continue."
            }
            _state.update { it.copy(error = message) }
            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isUploading = true,
                    phase = UploadPhase.UPLOADING,
                    progress = 0f,
                    error = null,
                    status = PaperStatus.UPLOADING
                )
            }
            try {
                // Simulate upload progress so the indicator visibly advances.
                for (step in 1..PROGRESS_STEPS) {
                    delay(PROGRESS_STEP_DELAY_MS)
                    _state.update { it.copy(progress = step.toFloat() / PROGRESS_STEPS) }
                }

                val paperId = uploadPaperUseCase(current.source, reference)
                _state.update {
                    it.copy(
                        isUploading = false,
                        progress = 1f,
                        paperId = paperId,
                        phase = UploadPhase.PROCESSING,
                        status = PaperStatus.PROCESSING
                    )
                }

                pollPaperStatusUseCase(paperId).collect { status ->
                    _state.update {
                        it.copy(
                            status = status,
                            phase = if (status == PaperStatus.COMPLETED) {
                                UploadPhase.COMPLETED
                            } else {
                                UploadPhase.PROCESSING
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isUploading = false,
                        phase = UploadPhase.FORM,
                        status = PaperStatus.FAILED,
                        error = e.message ?: "Upload failed. Please try again."
                    )
                }
            }
        }
    }

    private companion object {
        const val PROGRESS_STEPS = 5
        const val PROGRESS_STEP_DELAY_MS = 150L
    }
}
