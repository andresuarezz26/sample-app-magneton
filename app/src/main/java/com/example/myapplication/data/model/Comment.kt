package com.example.myapplication.data.model

data class Comment(
    val id: String,
    val username: String,
    val text: String,
    val timestampLabel: String,
    val likes: Int
)
