package com.example.myapplication.upload

import android.net.Uri

sealed interface UploadIntent {
    data class SelectTab(val index: Int) : UploadIntent
    data class PickPdf(val uri: Uri) : UploadIntent
    data class EnterDoi(val doi: String) : UploadIntent
    data object SubmitFile : UploadIntent
    data object SubmitDoi : UploadIntent
    data object Retry : UploadIntent
}
