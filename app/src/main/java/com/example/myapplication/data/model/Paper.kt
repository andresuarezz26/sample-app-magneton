package com.example.myapplication.data.model

/**
 * Where the paper originated from when the user started an upload.
 */
enum class UploadSource {
    FILE,
    DOI
}

/**
 * Backend processing lifecycle for an uploaded paper.
 */
enum class PaperStatus {
    UPLOADING,
    PROCESSING,
    COMPLETED,
    FAILED
}

/**
 * A scientific paper the user submitted for conversion into a short-form video.
 */
data class Paper(
    val id: String,
    val title: String,
    val sourceType: UploadSource,
    val status: PaperStatus
)
