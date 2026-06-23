package com.example.myapplication.profile

import com.example.myapplication.data.model.PaperVideo

data class ProfileUiState(
    val papers: List<PaperVideo> = emptyList(),
    val isLoading: Boolean = false
)
