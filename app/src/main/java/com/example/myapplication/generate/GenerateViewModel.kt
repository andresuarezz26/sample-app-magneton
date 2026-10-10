package com.example.myapplication.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.llm.GeneratedImageStore
import com.example.myapplication.domain.llm.LocalImageGenerator
import com.example.myapplication.domain.usecase.UploadPhotosUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenerateViewModel(
    private val imageGenerator: LocalImageGenerator,
    private val imageStore: GeneratedImageStore,
    private val uploadPhotosUseCase: UploadPhotosUseCase = UploadPhotosUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(GenerateUiState())
    val state: StateFlow<GenerateUiState> = _state.asStateFlow()

    fun onIntent(intent: GenerateIntent) {
        when (intent) {
            is GenerateIntent.PromptChanged -> _state.update { it.copy(prompt = intent.text) }
            GenerateIntent.Generate -> generate()
            GenerateIntent.UploadImage -> upload()
            GenerateIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
            GenerateIntent.StartOver -> _state.update { GenerateUiState() }
        }
    }

    private fun generate() {
        val current = _state.value
        if (current.prompt.isBlank() || current.isGenerating) return

        viewModelScope.launch {
            _state.update { it.copy(isGenerating = true, errorMessage = null, generatedImageUri = null) }
            imageGenerator.generate(current.prompt.trim())
                .mapCatching { imageStore.save(it) }
                .fold(
                    onSuccess = { uri ->
                        _state.update { it.copy(isGenerating = false, generatedImageUri = uri) }
                    },
                    onFailure = { error ->
                        _state.update { it.copy(isGenerating = false, errorMessage = error.message ?: "Generation failed") }
                    }
                )
        }
    }

    private fun upload() {
        val uri = _state.value.generatedImageUri ?: return
        if (_state.value.isUploading) return

        viewModelScope.launch {
            _state.update { it.copy(isUploading = true, errorMessage = null) }
            uploadPhotosUseCase(listOf(uri)).fold(
                onSuccess = {
                    _state.update { it.copy(isUploading = false, uploadSuccess = true) }
                },
                onFailure = { error ->
                    _state.update { it.copy(isUploading = false, errorMessage = error.message) }
                }
            )
        }
    }
}
