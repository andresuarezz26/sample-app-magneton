package com.example.myapplication.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Paper
import com.example.myapplication.data.model.ScientificField
import com.example.myapplication.domain.usecase.GetDiscoverFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DiscoverViewModel(
    private val getDiscoverFeedUseCase: GetDiscoverFeedUseCase = GetDiscoverFeedUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow<DiscoverUiState>(DiscoverUiState.Loading)
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    private var currentPageIndex = 0
    private var currentCategory: ScientificField? = null
    private var allLoadedPapers: List<Paper> = emptyList()

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
            _state.update { DiscoverUiState.Loading }
            currentPageIndex = 0
            currentCategory = null
            allLoadedPapers = emptyList()
            loadPage()
        }
    }

    private fun selectCategory(field: ScientificField?) {
        viewModelScope.launch {
            _state.update { DiscoverUiState.Loading }
            currentPageIndex = 0
            currentCategory = field
            allLoadedPapers = emptyList()
            loadPage()
        }
    }

    private fun loadNextPage() {
        val currentState = _state.value
        if (currentState !is DiscoverUiState.Success) return
        if (currentState.endReached) return

        viewModelScope.launch {
            _state.update {
                if (it is DiscoverUiState.Success) it.copy(isPaginating = true) else it
            }
            currentPageIndex++
            loadPage()
        }
    }

    private suspend fun loadPage() = withContext(Dispatchers.Main) {
        val newPapers = getDiscoverFeedUseCase(currentPageIndex, currentCategory)

        if (newPapers.isEmpty() && allLoadedPapers.isEmpty()) {
            _state.update { DiscoverUiState.Empty }
        } else {
            allLoadedPapers = allLoadedPapers + newPapers
            val endReached = newPapers.isEmpty() || newPapers.size < 3

            _state.update {
                DiscoverUiState.Success(
                    papers = allLoadedPapers,
                    selectedCategory = currentCategory,
                    isPaginating = false,
                    endReached = endReached
                )
            }
        }
    }
}
