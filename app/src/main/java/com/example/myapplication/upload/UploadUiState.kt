package com.example.myapplication.upload

data class UploadUiState(
    val selectedTab: Int = 0,
    val doiInput: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val paperId: String? = null
)
