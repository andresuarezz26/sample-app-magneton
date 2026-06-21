package com.example.myapplication.discover

import com.example.myapplication.data.model.VideoItem

data class DiscoverUiState(
    val availableTopics: List<String> = emptyList(),
    val selectedTopic: String? = null,
    val filteredVideos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false
)
