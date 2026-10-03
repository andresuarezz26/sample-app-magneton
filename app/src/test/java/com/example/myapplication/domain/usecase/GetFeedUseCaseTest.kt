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
    private val useCase = GetFeedUseCase(testDispatcher)

    @Test
    fun `returns a non-empty feed`() = runTest(testDispatcher) {
        assertTrue(useCase().isNotEmpty())
    }

    @Test
    fun `every video has a unique id`() = runTest(testDispatcher) {
        val videos = useCase()

        assertEquals(videos.size, videos.map { it.id }.toSet().size)
    }

    @Test
    fun `every video has non-blank author, description and music`() = runTest(testDispatcher) {
        useCase().forEach { video ->
            assertTrue(video.author.isNotBlank())
            assertTrue(video.description.isNotBlank())
            assertTrue(video.music.isNotBlank())
        }
    }

    @Test
    fun `returns the same feed on repeated calls`() = runTest(testDispatcher) {
        assertEquals(useCase(), useCase())
    }
}
