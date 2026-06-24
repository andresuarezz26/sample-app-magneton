package com.example.myapplication.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Paper
import com.example.myapplication.domain.usecase.GetDiscoverFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val getDiscoverFeedUseCase: GetDiscoverFeedUseCase = GetDiscoverFeedUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    private var currentPage = 0
    private var selectedCategory = "All"
    private var isLoadingNextPage = false

    init {
        onIntent(DiscoverIntent.LoadFeed)
    }

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.LoadFeed -> loadFeed()
            is DiscoverIntent.SelectCategory -> selectCategory(intent.field)
            DiscoverIntent.LoadNextPage -> loadNextPage()
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            try {
                _state.emit(DiscoverUiState.Loading)
                currentPage = 0
                isLoadingNextPage = false
                val papers = getDiscoverFeedUseCase(selectedCategory, currentPage)
                val hasMore = papers.size == GetDiscoverFeedUseCase.PAGE_SIZE
                _state.emit(
                    DiscoverUiState.Success(
                        papers = papers,
                        selectedCategory = selectedCategory,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                _state.emit(DiscoverUiState.Error(e.message ?: "Unknown error"))
            }
        }
    }

    private fun selectCategory(field: String) {
        selectedCategory = field
        loadFeed()
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState !is DiscoverUiState.Success) return
        if (!currentState.hasMore) return
        if (isLoadingNextPage) return

        viewModelScope.launch {
            try {
                isLoadingNextPage = true
                currentPage++
                val papers = getDiscoverFeedUseCase(selectedCategory, currentPage)
                val hasMore = papers.size == GetDiscoverFeedUseCase.PAGE_SIZE
                _state.emit(
                    currentState.copy(
                        papers = currentState.papers + papers,
                        hasMore = hasMore
                    )
                )
            } catch (e: Exception) {
                _state.emit(DiscoverUiState.Error(e.message ?: "Unknown error"))
            } finally {
                isLoadingNextPage = false
            }
        }
    }
}
