package com.example.myapplication.home

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
    data class SelectTopic(val topic: String) : HomeIntent
    data object ResetFilter : HomeIntent
}
