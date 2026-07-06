package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem

enum class FeedTab {
    FOR_YOU,
    FOLLOWING
}

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTab: FeedTab = FeedTab.FOR_YOU,
    val followedAuthors: Set<String> = emptySet()
) {
    val displayedVideos: List<VideoItem>
        get() = when (selectedTab) {
            FeedTab.FOR_YOU -> videos
            FeedTab.FOLLOWING -> videos.filter { it.author in followedAuthors }
        }
}
