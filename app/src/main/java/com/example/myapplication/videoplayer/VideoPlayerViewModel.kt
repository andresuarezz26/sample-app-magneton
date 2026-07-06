package com.example.myapplication.videoplayer

import androidx.lifecycle.ViewModel
import com.example.myapplication.data.model.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class VideoPlayerUiState(
    val videos: List<VideoItem> = emptyList(),
    val currentIndex: Int = 0,
    val isBottomSheetExpanded: Boolean = false,
    val likedVideos: Set<String> = emptySet(),
    val bookmarkedVideos: Set<String> = emptySet()
)

class VideoPlayerViewModel(
    initialVideos: List<VideoItem> = emptyList(),
    initialIndex: Int = 0
) : ViewModel() {

    private val _state = MutableStateFlow(VideoPlayerUiState(
        videos = initialVideos,
        currentIndex = initialIndex
    ))
    val state: StateFlow<VideoPlayerUiState> = _state.asStateFlow()

    fun onVideoIndexChanged(index: Int) {
        if (index in 0 until _state.value.videos.size) {
            _state.update { it.copy(currentIndex = index) }
        }
    }

    fun onBottomSheetStateChanged(isExpanded: Boolean) {
        _state.update { it.copy(isBottomSheetExpanded = isExpanded) }
    }

    fun toggleLike(videoId: String) {
        _state.update { current ->
            val newLiked = current.likedVideos.toMutableSet()
            if (newLiked.contains(videoId)) {
                newLiked.remove(videoId)
            } else {
                newLiked.add(videoId)
            }
            current.copy(likedVideos = newLiked)
        }
    }

    fun toggleBookmark(videoId: String) {
        _state.update { current ->
            val newBookmarked = current.bookmarkedVideos.toMutableSet()
            if (newBookmarked.contains(videoId)) {
                newBookmarked.remove(videoId)
            } else {
                newBookmarked.add(videoId)
            }
            current.copy(bookmarkedVideos = newBookmarked)
        }
    }

    fun onShare(videoId: String) {
        // Mock share action - in real app would trigger share intent
    }
}
