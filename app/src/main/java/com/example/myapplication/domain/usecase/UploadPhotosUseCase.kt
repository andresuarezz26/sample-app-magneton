package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class UploadPhotosUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(photoUris: List<String>): Result<Unit> = withContext(ioDispatcher) {
        delay(1200)
        Result.success(Unit)
    }
}
