package com.example.myapplication.discover

sealed interface DiscoverIntent {
    data object LoadFeed : DiscoverIntent
    data object LoadNextPage : DiscoverIntent
    data class SelectCategory(val field: String?) : DiscoverIntent
    data object Retry : DiscoverIntent
}
