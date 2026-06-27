package com.example.myapplication.home

import com.example.myapplication.data.model.FeedTab
import com.example.myapplication.data.model.VideoItem

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val currentFeedTab: FeedTab = FeedTab.ForYou,
    val followedUserIds: Set<String> = emptySet(),
    val selectedProfileUserId: String? = null
)
