package com.example.myapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.domain.repository.FollowRepository
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeedUseCase: GetFeedUseCase = GetFeedUseCase(),
    private val followRepository: FollowRepository = FollowRepository()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        followRepository.followedAuthors
            .onEach { followed -> _state.update { it.copy(followedAuthors = followed) } }
            .launchIn(viewModelScope)
        onIntent(HomeIntent.LoadFeed)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.LikeVideo -> likeVideo(intent.videoId)
            is HomeIntent.ToggleFollow -> toggleFollow(intent.author)
            is HomeIntent.SelectTab -> selectTab(intent.tab)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val videos = getFeedUseCase()
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

    private fun toggleFollow(author: String) {
        followRepository.toggleFollow(author)
    }

    private fun selectTab(tab: FeedTab) {
        _state.update { it.copy(selectedTab = tab) }
    }
}
