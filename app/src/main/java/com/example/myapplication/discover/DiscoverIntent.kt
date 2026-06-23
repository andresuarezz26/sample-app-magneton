package com.example.myapplication.discover

import com.example.myapplication.data.model.ScientificField

sealed interface DiscoverIntent {
    data object LoadFeed : DiscoverIntent
    data class SelectCategory(val field: ScientificField?) : DiscoverIntent
    data object LoadNextPage : DiscoverIntent
}
