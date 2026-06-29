package com.example.myapplication.home

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
    data class SelectTab(val tab: Tab) : HomeIntent
    data class SaveVideo(val videoId: String) : HomeIntent
    data class RemoveBookmark(val videoId: String) : HomeIntent
}
