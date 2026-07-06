package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTopic: String? = null
) {
    val filteredVideos: List<VideoItem>
        get() = if (selectedTopic == null || selectedTopic == "All") videos else videos.filter { it.topic == selectedTopic }
}
