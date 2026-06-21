package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFeedUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `all videos have extracted topics from hashtags in description`() = runTest {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()

        assertEquals(6, videos.size)
        videos.forEach { video ->
            assertTrue("Video ${video.id} should have at least one topic", video.topics.isNotEmpty())
        }
    }

    @Test
    fun `video with multiple hashtags has all topics extracted`() = runTest {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()
        val physicsVideos = videos.filter { it.topics.contains("physics") }

        assertEquals(2, physicsVideos.size)
        val spaceVideo = videos.find { it.id == "1" }
        assertEquals(listOf("space", "physics"), spaceVideo?.topics)
    }

    @Test
    fun `topics are extracted correctly from all videos`() = runTest {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()

        videos.forEach { video ->
            when (video.id) {
                "1" -> assertEquals(listOf("space", "physics"), video.topics)
                "2" -> assertEquals(listOf("quantum", "physics"), video.topics)
                "3" -> assertEquals(listOf("biology"), video.topics)
                "4" -> assertEquals(listOf("earth"), video.topics)
                "5" -> assertEquals(listOf("neuroscience"), video.topics)
                "6" -> assertEquals(listOf("chemistry"), video.topics)
            }
        }
    }
}
