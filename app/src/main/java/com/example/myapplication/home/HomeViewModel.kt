package com.example.myapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeedUseCase: GetFeedUseCase = GetFeedUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()
    private var filterTopic: String? = null

    init {
        onIntent(HomeIntent.LoadFeed)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.LikeVideo -> likeVideo(intent.videoId)
        }
    }

    fun setTopicFilter(topic: String?) {
        filterTopic = topic
        loadFeed()
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            var videos = getFeedUseCase()
            filterTopic?.let { topic ->
                videos = videos.filter { it.topics.contains(topic) }
            }
            _state.update { it.copy(videos = videos, isLoading = false) }
        }
    }

    private fun likeVideo(videoId: String) {
        _state.update { current ->
            current.copy(
                videos = current.videos.map { video ->
                    if (video.id == videoId) video.copy(likes = video.likes + 1) else video
                }
            )
        }
    }
}
