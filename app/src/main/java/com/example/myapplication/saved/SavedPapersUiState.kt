package com.example.myapplication.saved

import com.example.myapplication.data.model.VideoItem

data class SavedPapersUiState(
    val papers: List<VideoItem> = emptyList()
)
