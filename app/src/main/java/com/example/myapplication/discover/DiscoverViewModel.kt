package com.example.myapplication.discover

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.usecase.GetFeedUseCase
import com.example.myapplication.domain.usecase.GetTopicsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DiscoverViewModel(
    private val getTopicsUseCase: GetTopicsUseCase,
    private val getFeedUseCase: GetFeedUseCase
) : ViewModel() {

    constructor(
        getFeedUseCase: GetFeedUseCase = GetFeedUseCase(),
        ioDispatcher: kotlin.coroutines.CoroutineContext = kotlinx.coroutines.Dispatchers.IO
    ) : this(
        GetTopicsUseCase(getFeedUseCase, ioDispatcher),
        getFeedUseCase
    )

    private val _state = MutableStateFlow(DiscoverUiState())
    val state: StateFlow<DiscoverUiState> = _state.asStateFlow()

    init {
        onIntent(DiscoverIntent.LoadTopics)
    }

    fun onIntent(intent: DiscoverIntent) {
        when (intent) {
            DiscoverIntent.LoadTopics -> loadTopics()
            is DiscoverIntent.SelectTopic -> selectTopic(intent.topic)
            DiscoverIntent.ClearSelection -> clearSelection()
        }
    }

    private fun loadTopics() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val topics = getTopicsUseCase()
            val videos = getFeedUseCase()
            _state.update {
                it.copy(
                    availableTopics = topics,
                    filteredVideos = videos,
                    isLoading = false
                )
            }
        }
    }

    private fun selectTopic(topic: String) {
        viewModelScope.launch {
            val videos = getFeedUseCase()
            val filtered = videos.filter { it.topics.contains(topic) }
            _state.update {
                it.copy(selectedTopic = topic, filteredVideos = filtered)
            }
        }
    }

    private fun clearSelection() {
        viewModelScope.launch {
            val videos = getFeedUseCase()
            _state.update {
                it.copy(selectedTopic = null, filteredVideos = videos)
            }
        }
    }
}
