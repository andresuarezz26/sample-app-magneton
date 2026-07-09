package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.PaperStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * Mock status-polling use case.
 *
 * Emits [PaperStatus.PROCESSING] and then, after a few 5s poll intervals (30-120s of real
 * backend work), emits [PaperStatus.COMPLETED]. Uses `delay`, so virtual time in tests drives
 * it instantly while real usage waits between polls.
 */
class PollPaperStatusUseCase(
    private val pollIntervalMs: Long = 5_000L,
    private val processingPolls: Int = 3
) {

    operator fun invoke(paperId: String): Flow<PaperStatus> = flow {
        repeat(processingPolls) {
            emit(PaperStatus.PROCESSING)
            delay(pollIntervalMs)
        }
        emit(PaperStatus.COMPLETED)
    }
}
