package com.example.myapplication.home

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data object RefreshFeed : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
}
