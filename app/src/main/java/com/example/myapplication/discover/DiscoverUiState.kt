package com.example.myapplication.discover

import com.example.myapplication.data.model.Paper

sealed class DiscoverUiState {
    data object Loading : DiscoverUiState()
    data class Success(
        val papers: List<Paper>,
        val selectedCategory: String = "All",
        val hasMore: Boolean = true
    ) : DiscoverUiState()
    data class Error(val message: String) : DiscoverUiState()
}
