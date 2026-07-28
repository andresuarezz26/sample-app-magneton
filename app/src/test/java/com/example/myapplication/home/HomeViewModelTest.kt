package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
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
class HomeViewModelTest {

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
    fun `initial load populates feed with 6 videos and isLoading is false`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
    }

    @Test
    fun `liking a video increments only that video likes by 1`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetId = "1"
        val videosBefore = viewModel.state.value.videos.toList()
        val initialLikes = videosBefore.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        val videosAfter = viewModel.state.value.videos
        assertEquals(initialLikes + 1, videosAfter.first { it.id == targetId }.likes)
        videosAfter.filter { it.id != targetId }.forEach { video ->
            val original = videosBefore.first { it.id == video.id }
            assertEquals(original.likes, video.likes)
        }
    }

    @Test
    fun `liking a video with unknown id leaves feed unchanged`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val videosBefore = viewModel.state.value.videos.toList()

        viewModel.onIntent(HomeIntent.LikeVideo("unknown-id-999"))

        assertEquals(videosBefore, viewModel.state.value.videos)
    }

    @Test
    fun `isLoading is true and videos empty while initial load is in flight`() = runTest(testDispatcher) {
        val fakeUseCase = FakeSlowGetFeedUseCase()
        val viewModel = HomeViewModel(fakeUseCase)
        testDispatcher.scheduler.runCurrent()

        val state = viewModel.state.value
        assertTrue(state.isLoading)
        assertTrue(state.videos.isEmpty())

        fakeUseCase.pending.complete(emptyList())
        advanceUntilIdle()
    }

    @Test
    fun `dispatching LoadFeed again reloads the feed`() = runTest(testDispatcher) {
        val fakeUseCase = FakeSlowGetFeedUseCase()
        val viewModel = HomeViewModel(fakeUseCase)
        testDispatcher.scheduler.runCurrent()
        fakeUseCase.pending.complete(sampleVideos)
        advanceUntilIdle()
        assertFalse(viewModel.state.value.isLoading)
        assertEquals(sampleVideos.size, viewModel.state.value.videos.size)

        fakeUseCase.pending = CompletableDeferred()
        viewModel.onIntent(HomeIntent.LoadFeed)
        testDispatcher.scheduler.runCurrent()

        assertTrue(viewModel.state.value.isLoading)

        fakeUseCase.pending.complete(sampleVideos)
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(sampleVideos.size, viewModel.state.value.videos.size)
    }

    @Test
    fun `liking the same video twice increments likes by 2`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetId = "1"
        val videosBefore = viewModel.state.value.videos.toList()
        val initialLikes = videosBefore.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        val videosAfter = viewModel.state.value.videos
        assertEquals(initialLikes + 2, videosAfter.first { it.id == targetId }.likes)
        videosAfter.filter { it.id != targetId }.forEach { video ->
            val original = videosBefore.first { it.id == video.id }
            assertEquals(original.likes, video.likes)
        }
    }

    private val sampleVideos = listOf(
        VideoItem(
            id = "1",
            author = "@author",
            description = "description",
            likes = 0,
            comments = 0,
            shares = 0,
            music = "music",
            backgroundColorHex = 0xFF000000
        )
    )

    private class FakeSlowGetFeedUseCase : GetFeedUseCase() {
        var pending = CompletableDeferred<List<VideoItem>>()

        override suspend fun invoke(): List<VideoItem> = pending.await()
    }
}
