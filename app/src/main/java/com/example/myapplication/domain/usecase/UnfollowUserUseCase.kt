package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class UnfollowUserUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {
    suspend operator fun invoke(followUserId: String): Unit = withContext(ioDispatcher) {
        // Mock implementation: in a real app, this would DELETE /api/v1/users/{id}/follow
    }
}
