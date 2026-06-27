package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.VideoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetFollowingFeedUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO,
    private val getFeedUseCase: GetFeedUseCase = GetFeedUseCase()
) {
    suspend operator fun invoke(followedUserIds: Set<String>): List<VideoItem> = withContext(ioDispatcher) {
        val allVideos = getFeedUseCase()
        allVideos.filter { it.authorId in followedUserIds }
    }
}
