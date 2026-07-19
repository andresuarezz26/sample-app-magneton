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

    private var allVideos: List<VideoItem> = emptyList()
    private val topicAffinity: MutableMap<String, Int> = mutableMapOf()

    init {
        onIntent(HomeIntent.LoadFeed)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.LikeVideo -> likeVideo(intent.videoId)
            is HomeIntent.SelectTopic -> selectTopic(intent.topic)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            allVideos = getFeedUseCase()
            val topics = allVideos.map { it.topic }.distinct()
            _state.update {
                it.copy(
                    videos = visibleVideos(it.selectedTopic),
                    topics = topics,
                    isLoading = false
                )
            }
        }
    }

    private fun likeVideo(videoId: String) {
        val likedVideo = allVideos.firstOrNull { it.id == videoId } ?: return
        allVideos = allVideos.map { video ->
            if (video.id == videoId) video.copy(likes = video.likes + 1) else video
        }
        topicAffinity[likedVideo.topic] = (topicAffinity[likedVideo.topic] ?: 0) + 1
        // Only bump the liked video's like count in place; the affinity-based
        // reorder of the 'For You' list is applied on the next load/topic
        // selection so the pager's current page keeps pointing at the same video.
        _state.update { current ->
            current.copy(
                videos = current.videos.map { video ->
                    if (video.id == videoId) video.copy(likes = video.likes + 1) else video
                }
            )
        }
    }

    private fun selectTopic(topic: String?) {
        _state.update {
            it.copy(selectedTopic = topic, videos = visibleVideos(topic))
        }
    }

    private fun visibleVideos(topic: String?): List<VideoItem> {
        return if (topic == null) {
            allVideos.sortedByDescending { video -> topicAffinity[video.topic] ?: 0 }
        } else {
            allVideos.filter { it.topic == topic }
        }
    }
}
