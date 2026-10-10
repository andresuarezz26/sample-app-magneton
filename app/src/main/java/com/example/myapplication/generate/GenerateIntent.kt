package com.example.myapplication.generate

sealed interface GenerateIntent {
    data class PromptChanged(val text: String) : GenerateIntent
    data object Generate : GenerateIntent
    data object UploadImage : GenerateIntent
    data object DismissError : GenerateIntent
    data object StartOver : GenerateIntent
}
