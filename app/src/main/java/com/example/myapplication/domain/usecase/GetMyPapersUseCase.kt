package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.PaperVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetMyPapersUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<PaperVideo> = withContext(ioDispatcher) {
        listOf(
            PaperVideo(
                id = "paper_123456",
                title = "Quantum Computing and Climate Modeling",
                doi = "10.1038/s41586-021-03819-2",
                videoUrl = "https://example.com/video1.mp4",
                thumbnail = "https://example.com/thumb1.jpg",
                processingTimeSeconds = 45
            ),
            PaperVideo(
                id = "paper_234567",
                title = "CRISPR Gene Editing: Recent Advances",
                doi = "10.1126/science.abb7295",
                videoUrl = "https://example.com/video2.mp4",
                thumbnail = "https://example.com/thumb2.jpg",
                processingTimeSeconds = 62
            )
        )
    }
}
