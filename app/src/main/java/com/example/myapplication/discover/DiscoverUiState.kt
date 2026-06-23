package com.example.myapplication.discover

import com.example.myapplication.data.model.Paper
import com.example.myapplication.data.model.ScientificField

sealed interface DiscoverUiState {
    data object Loading : DiscoverUiState
    data class Success(
        val papers: List<Paper>,
        val selectedCategory: ScientificField? = null,
        val isPaginating: Boolean = false,
        val endReached: Boolean = false
    ) : DiscoverUiState
    data class Error(val message: String) : DiscoverUiState
    data object Empty : DiscoverUiState
}
