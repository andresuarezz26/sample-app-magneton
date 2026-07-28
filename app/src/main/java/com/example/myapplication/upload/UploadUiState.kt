package com.example.myapplication.upload

data class UploadUiState(
    val selectedPhotos: List<String> = emptyList(),
    val isUploading: Boolean = false,
    val uploadSuccess: Boolean = false,
    val errorMessage: String? = null
)
