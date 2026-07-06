package com.example.myapplication.discover

import com.example.myapplication.data.model.Paper

sealed class DiscoverUiState {
    data object Loading : DiscoverUiState()

    data class Success(
        val papers: List<Paper> = emptyList(),
        val categories: List<String> = emptyList(),
        val selectedCategory: String? = null,
        val isLoadingNextPage: Boolean = false,
        val endReached: Boolean = false
    ) : DiscoverUiState()

    data class Error(val message: String) : DiscoverUiState()
}
