package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.UploadSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Mock upload use case mirroring [GetFeedUseCase].
 *
 * Accepts a PDF [android.net.Uri] (as a string) or a DOI/arXiv URL and "uploads" it to the
 * backend, returning the id the backend assigned to the created paper. Real networking is
 * out of scope for this skeleton, so the id is derived deterministically from the reference.
 */
open class UploadPaperUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    open suspend operator fun invoke(source: UploadSource, reference: String): String =
        withContext(ioDispatcher) {
            require(reference.isNotBlank()) { "A PDF file or DOI is required to upload." }
            val prefix = when (source) {
                UploadSource.FILE -> "file"
                UploadSource.DOI -> "doi"
            }
            "paper-$prefix-${reference.hashCode().toUInt()}"
        }
}
