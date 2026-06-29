package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem

enum class Tab {
    Following, ForYou, Saved
}

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: Tab = Tab.ForYou,
    val savedVideoIds: Set<String> = emptySet()
)
