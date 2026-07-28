package com.example.myapplication.comment

import com.example.myapplication.data.model.Comment

data class CommentUiState(
    val isLoading: Boolean = true,
    val comments: List<Comment> = emptyList()
)
