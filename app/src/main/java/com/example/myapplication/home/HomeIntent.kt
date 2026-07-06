package com.example.myapplication.home

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
    data class ToggleFollow(val author: String) : HomeIntent
    data class SelectTab(val tab: FeedTab) : HomeIntent
}
