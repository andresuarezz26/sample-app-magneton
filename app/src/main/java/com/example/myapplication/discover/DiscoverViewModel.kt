package com.example.myapplication.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetDiscoverFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private const val PAGE_SIZE = 10

class DiscoverViewModel(
    private val getDiscoverFeedUseCase: GetDiscoverFeedUseCase = GetDiscoverFeedUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    private var currentPage = 0
    private var selectedCategory: String? = null

    init {
        onIntent(DiscoverIntent.LoadFeed)
    }

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.LoadFeed -> loadFeed()
            DiscoverIntent.LoadNextPage -> loadNextPage()
            is DiscoverIntent.SelectCategory -> {
                selectedCategory = intent.field
                loadFeed()
            }
            DiscoverIntent.Retry -> loadFeed()
        }
    }

    private fun loadFeed() {
        currentPage = 0
        viewModelScope.launch {
            _state.value = DiscoverUiState.Loading
            runCatching {
                getDiscoverFeedUseCase(page = currentPage, pageSize = PAGE_SIZE, category = selectedCategory)
            }.onSuccess { result ->
                _state.value = DiscoverUiState.Success(
                    papers = result.papers,
                    categories = getDiscoverFeedUseCase.categories,
                    selectedCategory = selectedCategory,
                    endReached = !result.hasMore
                )
            }.onFailure { error ->
                _state.value = DiscoverUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }

    private fun loadNextPage() {
        val current = _state.value as? DiscoverUiState.Success ?: return
        if (current.isLoadingNextPage || current.endReached) return

        viewModelScope.launch {
            _state.update { (it as? DiscoverUiState.Success)?.copy(isLoadingNextPage = true) ?: it }
            val nextPage = currentPage + 1
            runCatching {
                getDiscoverFeedUseCase(page = nextPage, pageSize = PAGE_SIZE, category = selectedCategory)
            }.onSuccess { result ->
                currentPage = nextPage
                _state.update { state ->
                    (state as? DiscoverUiState.Success)?.copy(
                        papers = state.papers + result.papers,
                        isLoadingNextPage = false,
                        endReached = !result.hasMore
                    ) ?: state
                }
            }.onFailure { error ->
                _state.value = DiscoverUiState.Error(error.message ?: "Something went wrong")
            }
        }
    }
}
