package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.PaperStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class PollPaperStatusUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    operator fun invoke(paperId: String): Flow<PaperStatus> = flow {
        emit(PaperStatus.QUEUED)
        delay(2000)
        emit(PaperStatus.PROCESSING)
        repeat(4) {
            delay(5000)
            emit(PaperStatus.PROCESSING)
        }
        emit(PaperStatus.COMPLETED)
    }.flowOn(ioDispatcher)
}
