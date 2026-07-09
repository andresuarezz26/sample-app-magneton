package com.example.myapplication.upload

import android.net.Uri
import com.example.myapplication.data.model.UploadSource

sealed interface UploadIntent {
    data class SelectSource(val source: UploadSource) : UploadIntent
    data class SelectPdf(val uri: Uri, val displayName: String? = null) : UploadIntent
    data class EnterDoi(val value: String) : UploadIntent
    data object Submit : UploadIntent
    data object Retry : UploadIntent
}
