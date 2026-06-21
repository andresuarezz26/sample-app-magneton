package com.example.myapplication.discover

sealed class DiscoverIntent {
    data object LoadTopics : DiscoverIntent()
    data class SelectTopic(val topic: String) : DiscoverIntent()
    data object ClearSelection : DiscoverIntent()
}
