package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetTopicsUseCase(
    private val getFeedUseCase: GetFeedUseCase,
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<String> = withContext(ioDispatcher) {
        val videos = getFeedUseCase()
        videos.flatMap { it.topics }.distinct().sorted()
    }
}
