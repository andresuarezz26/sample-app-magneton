package com.example.myapplication.comment

sealed interface CommentIntent {
    data object LoadComments : CommentIntent
}
