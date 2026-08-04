package com.example.myapplication.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetFeedUseCaseTest {

    private val testDispatcher = StandardTestDispatcher()

    private val getFeedUseCase = GetFeedUseCase(testDispatcher)

    @Test
    fun `returns a feed of 6 videos`() = runTest(testDispatcher) {
        assertEquals(6, getFeedUseCase().size)
    }

    @Test
    fun `every video has a unique id`() = runTest(testDispatcher) {
        val videos = getFeedUseCase()
        assertEquals(videos.size, videos.map { it.id }.toSet().size)
    }

    @Test
    fun `returns the same feed on every invocation`() = runTest(testDispatcher) {
        assertEquals(getFeedUseCase(), getFeedUseCase())
    }

    @Test
    fun `every video has non-blank content fields and non-negative counters`() =
        runTest(testDispatcher) {
            getFeedUseCase().forEach { video ->
                assertTrue(video.id.isNotBlank())
                assertTrue(video.author.isNotBlank())
                assertTrue(video.description.isNotBlank())
                assertTrue(video.music.isNotBlank())
                assertTrue(video.likes >= 0)
                assertTrue(video.comments >= 0)
                assertTrue(video.shares >= 0)
            }
        }

    @Test
    fun `first video matches the expected feed entry`() = runTest(testDispatcher) {
        val first = getFeedUseCase().first()
        assertEquals("1", first.id)
        assertEquals("@astro_facts", first.author)
        assertEquals(48200, first.likes)
        assertEquals(1203, first.comments)
        assertEquals(892, first.shares)
        assertEquals("Cosmic Vibes — Science Beats", first.music)
        assertEquals(0xFF1A1A2E, first.backgroundColorHex)
    }

    @Test
    fun `uses the default IO dispatcher when none is provided`() = runTest {
        assertEquals(6, GetFeedUseCase()().size)
    }
}
