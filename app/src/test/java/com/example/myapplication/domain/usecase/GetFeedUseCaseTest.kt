package com.example.myapplication.domain.usecase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun `returns exactly 6 videos`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()

        assertEquals(6, videos.size)
    }

    @Test
    fun `returned ids are unique`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()

        assertEquals(videos.size, videos.map { it.id }.distinct().size)
    }

    @Test
    fun `each video has non-blank text fields and non-negative counters`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)

        val videos = getFeedUseCase()

        videos.forEach { video ->
            assertTrue(video.author.isNotBlank())
            assertTrue(video.description.isNotBlank())
            assertTrue(video.music.isNotBlank())
            assertFalse(video.likes < 0)
            assertFalse(video.comments < 0)
            assertFalse(video.shares < 0)
        }
    }
}
