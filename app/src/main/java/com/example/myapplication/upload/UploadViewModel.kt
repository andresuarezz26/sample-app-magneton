package com.example.myapplication.upload

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.UploadPaperUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadViewModel(
    private val uploadPaperUseCase: UploadPaperUseCase = UploadPaperUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    private var selectedPdfUri: Uri? = null

    fun onIntent(intent: UploadIntent) {
        when (intent) {
            is UploadIntent.SelectTab -> _state.update { it.copy(selectedTab = intent.index) }
            is UploadIntent.PickPdf -> {
                selectedPdfUri = intent.uri
                _state.update { it.copy(error = null) }
            }
            is UploadIntent.EnterDoi -> _state.update { it.copy(doiInput = intent.doi, error = null) }
            UploadIntent.SubmitFile -> submitFile()
            UploadIntent.SubmitDoi -> submitDoi()
            UploadIntent.Retry -> {
                _state.update { it.copy(error = null, paperId = null) }
                if (selectedPdfUri != null) {
                    submitFile()
                } else if (_state.value.doiInput.isNotEmpty()) {
                    submitDoi()
                }
            }
        }
    }

    private fun submitFile() {
        if (selectedPdfUri == null) {
            _state.update { it.copy(error = "Please select a PDF file") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val paperId = uploadPaperUseCase(pdfUri = selectedPdfUri)
                _state.update { it.copy(paperId = paperId, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Upload failed", isLoading = false) }
            }
        }
    }

    private fun submitDoi() {
        val doi = _state.value.doiInput.trim()
        if (doi.isEmpty()) {
            _state.update { it.copy(error = "Please enter a DOI") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val paperId = uploadPaperUseCase(doi = doi)
                _state.update { it.copy(paperId = paperId, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message ?: "Upload failed", isLoading = false) }
            }
        }
    }
}
