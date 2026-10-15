package com.example.myapplication.home

sealed interface HomeIntent {
    data object LoadFeed : HomeIntent
    data object Retry : HomeIntent
    data class LikeVideo(val videoId: String) : HomeIntent
}
