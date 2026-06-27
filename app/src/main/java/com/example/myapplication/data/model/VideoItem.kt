package com.example.myapplication.data.model

data class VideoItem(
    val id: String,
    val author: String,
    val description: String,
    val likes: Int,
    val comments: Int,
    val shares: Int,
    val music: String,
    val backgroundColorHex: Long,
    val authorId: String = ""
)
