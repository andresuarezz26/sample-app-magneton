package com.example.myapplication.generate

data class GenerateUiState(
    val prompt: String = "",
    val isGenerating: Boolean = false,
    val generatedImageUri: String? = null,
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)
