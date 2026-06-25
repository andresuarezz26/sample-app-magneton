package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * Mock use case simulating POST /api/v1/users/me/interests.
 * No-op that simply accepts the selected field ids.
 */
class SaveInterestsUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(fieldIds: Set<String>, institutionalEmail: String?) =
        withContext(ioDispatcher) {
            // Mock: in a real implementation this would POST the selected field ids
            // (and optional institutional email) to the backend.
        }
}
