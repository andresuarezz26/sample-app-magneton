package com.example.myapplication.upload

import android.net.Uri
import com.example.myapplication.data.model.PaperStatus
import com.example.myapplication.data.model.UploadSource

/**
 * Which screen of the self-contained upload flow is currently visible.
 */
enum class UploadPhase {
    FORM,
    UPLOADING,
    PROCESSING,
    COMPLETED
}

data class UploadUiState(
    val source: UploadSource = UploadSource.FILE,
    val doi: String = "",
    val selectedPdf: Uri? = null,
    val selectedPdfName: String? = null,
    val progress: Float = 0f,
    val isUploading: Boolean = false,
    val phase: UploadPhase = UploadPhase.FORM,
    val status: PaperStatus? = null,
    val paperId: String? = null,
    val error: String? = null
) {
    /** Whether the current form has enough input to submit. */
    val canSubmit: Boolean
        get() = when (source) {
            UploadSource.DOI -> doi.isNotBlank()
            UploadSource.FILE -> selectedPdf != null
        }
}
