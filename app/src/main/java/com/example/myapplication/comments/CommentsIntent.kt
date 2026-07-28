package com.example.myapplication.comments

sealed interface CommentsIntent {
    data class LoadComments(val videoId: String) : CommentsIntent
    data class LikeComment(val commentId: String) : CommentsIntent
}
