package com.example.myapplication.data.model

data class PaperVideo(
    val id: String,
    val title: String,
    val doi: String,
    val videoUrl: String,
    val thumbnail: String = "",
    val processingTimeSeconds: Int = 0
)
