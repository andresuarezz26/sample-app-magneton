package com.example.myapplication.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.UploadPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UploadViewModel(
    private val uploadPhotosUseCase: UploadPhotosUseCase = UploadPhotosUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(UploadUiState())
    val state: StateFlow<UploadUiState> = _state.asStateFlow()

    fun onIntent(intent: UploadIntent) {
        when (intent) {
            is UploadIntent.PhotosSelected -> selectPhotos(intent.uris)
            is UploadIntent.RemovePhoto -> removePhoto(intent.uri)
            UploadIntent.UploadPhotos -> uploadPhotos()
            UploadIntent.DismissError -> dismissError()
            UploadIntent.StartOver -> startOver()
        }
    }

    private fun selectPhotos(uris: List<String>) {
        _state.update { current ->
            current.copy(selectedPhotos = (current.selectedPhotos + uris).distinct())
        }
    }

    private fun removePhoto(uri: String) {
        _state.update { current ->
            current.copy(selectedPhotos = current.selectedPhotos.filter { it != uri })
        }
    }

    private fun uploadPhotos() {
        val photos = _state.value.selectedPhotos
        if (photos.isEmpty()) return

        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, errorMessage = null) }
            val result = uploadPhotosUseCase(photos)
            result.fold(
                onSuccess = {
                    _state.update { it.copy(isUploading = false, uploadSuccess = true) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isUploading = false, errorMessage = error.message) }
                }
            )
        }
    }

    private fun dismissError() {
        _state.update { it.copy(errorMessage = null) }
    }

    private fun startOver() {
        _state.update { UploadUiState() }
    }
}
