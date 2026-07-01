package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem

data class HomeUiState(
    val videos: List<VideoItem> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false
)
