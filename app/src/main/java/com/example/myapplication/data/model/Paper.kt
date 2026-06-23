package com.example.myapplication.data.model

data class Paper(
    val id: String,
    val title: String,
    val doi: String,
    val status: PaperStatus,
    val videoUrl: String? = null
)
