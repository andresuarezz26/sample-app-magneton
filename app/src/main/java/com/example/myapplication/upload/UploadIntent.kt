package com.example.myapplication.upload

sealed interface UploadIntent {
    data class PhotosSelected(val uris: List<String>) : UploadIntent
    data class RemovePhoto(val uri: String) : UploadIntent
    data object UploadPhotos : UploadIntent
    data object DismissError : UploadIntent
    data object StartOver : UploadIntent
}
