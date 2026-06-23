package com.example.myapplication.domain.usecase

import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

class UploadPaperUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(pdfUri: Uri? = null, doi: String? = null): String = withContext(ioDispatcher) {
        delay(500)
        val paperId = "paper_${Random.nextLong(100000, 999999)}"
        paperId
    }
}
