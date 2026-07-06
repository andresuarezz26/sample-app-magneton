package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class SubmitInterestsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(fieldIds: List<String>) = withContext(ioDispatcher) {
        // POST /api/v1/users/me/interests — mocked, no networking layer exists yet.
    }
}
