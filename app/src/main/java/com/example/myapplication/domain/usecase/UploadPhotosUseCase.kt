package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

interface UploadPhotosUseCase {
    suspend operator fun invoke(photoUris: List<String>): Result<Unit>
}

class UploadPhotosUseCaseImpl(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) : UploadPhotosUseCase {

    override suspend operator fun invoke(photoUris: List<String>): Result<Unit> = withContext(ioDispatcher) {
        delay(1200)
        Result.success(Unit)
    }
}
