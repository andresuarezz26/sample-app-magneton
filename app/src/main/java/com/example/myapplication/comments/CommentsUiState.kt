package com.example.myapplication.comments

import com.example.myapplication.data.model.Comment

data class CommentsUiState(
    val isLoading: Boolean = false,
    val comments: List<Comment> = emptyList(),
    val errorMessage: String? = null
)
