package com.example.myapplication.data.model

data class Paper(
    val id: String,
    val title: String,
    val authors: List<String>,
    val field: String,
    val abstract: String,
    val likes: Int
)
