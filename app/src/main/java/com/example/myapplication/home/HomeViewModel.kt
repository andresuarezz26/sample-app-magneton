package com.example.myapplication.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.FeedTab
import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.FollowUserUseCase
import com.example.myapplication.domain.usecase.GetFeedUseCase
import com.example.myapplication.domain.usecase.GetFollowingFeedUseCase
import com.example.myapplication.domain.usecase.UnfollowUserUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getFeedUseCase: GetFeedUseCase = GetFeedUseCase(),
    private val getFollowingFeedUseCase: GetFollowingFeedUseCase = GetFollowingFeedUseCase(),
    private val followUserUseCase: FollowUserUseCase = FollowUserUseCase(),
    private val unfollowUserUseCase: UnfollowUserUseCase = UnfollowUserUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        onIntent(HomeIntent.LoadFeed)
    }

    fun onIntent(intent: HomeIntent) {
        when (intent) {
            HomeIntent.LoadFeed -> loadFeed()
            is HomeIntent.LikeVideo -> likeVideo(intent.videoId)
            is HomeIntent.SwitchFeedTab -> switchFeedTab(intent.tab)
            is HomeIntent.FollowUser -> followUser(intent.userId)
            is HomeIntent.UnfollowUser -> unfollowUser(intent.userId)
            is HomeIntent.ShowProfileModal -> showProfileModal(intent.userId)
            HomeIntent.HideProfileModal -> hideProfileModal()
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val videos = when (_state.value.currentFeedTab) {
                FeedTab.ForYou -> getFeedUseCase()
                FeedTab.Following -> getFollowingFeedUseCase(_state.value.followedUserIds)
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

    private fun switchFeedTab(tab: FeedTab) {
        _state.update { it.copy(currentFeedTab = tab) }
        loadFeed()
    }

    private fun followUser(userId: String) {
        viewModelScope.launch {
            followUserUseCase(userId)
            _state.update { current ->
                current.copy(followedUserIds = current.followedUserIds + userId)
            }
            loadFeed()
        }
    }

    private fun unfollowUser(userId: String) {
        viewModelScope.launch {
            unfollowUserUseCase(userId)
            _state.update { current ->
                current.copy(followedUserIds = current.followedUserIds - userId)
            }
            loadFeed()
        }
    }

    private fun showProfileModal(userId: String) {
        _state.update { it.copy(selectedProfileUserId = userId) }
    }

    private fun hideProfileModal() {
        _state.update { it.copy(selectedProfileUserId = null) }
    }
}
