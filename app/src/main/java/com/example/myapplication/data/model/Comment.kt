package com.example.myapplication.data.model

data class Comment(
    val id: String,
    val videoId: String,
    val author: String,
    val text: String,
    val likes: Int,
    val timestampLabel: String
)
