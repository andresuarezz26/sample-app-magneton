package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
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
    fun `state starts as the default HomeUiState before the init coroutine runs`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(GetFeedUseCase(testDispatcher))

        assertEquals(HomeUiState(), viewModel.state.value)

        advanceUntilIdle()
    }

    @Test
    fun `loadFeed sets isLoading while the feed request is in flight`() = runTest(testDispatcher) {
        val feedScheduler = TestCoroutineScheduler()
        val viewModel = HomeViewModel(GetFeedUseCase(StandardTestDispatcher(feedScheduler)))

        advanceUntilIdle()
        assertTrue(viewModel.state.value.isLoading)
        assertTrue(viewModel.state.value.videos.isEmpty())

        feedScheduler.advanceUntilIdle()
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(6, viewModel.state.value.videos.size)
    }

    @Test
    fun `LoadFeed intent reloads the feed and discards local like state`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()
        val originalLikes = viewModel.state.value.videos.first { it.id == "1" }.likes
        viewModel.onIntent(HomeIntent.LikeVideo("1"))

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
        assertEquals(originalLikes, state.videos.first { it.id == "1" }.likes)
    }

    @Test
    fun `liking the same video twice increments likes by 2`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(GetFeedUseCase(testDispatcher))
        advanceUntilIdle()
        val before = viewModel.state.value.videos.first { it.id == "3" }.likes

        viewModel.onIntent(HomeIntent.LikeVideo("3"))
        viewModel.onIntent(HomeIntent.LikeVideo("3"))

        assertEquals(before + 2, viewModel.state.value.videos.first { it.id == "3" }.likes)
    }

    @Test
    fun `liking a video preserves order and every other field`() = runTest(testDispatcher) {
        val viewModel = HomeViewModel(GetFeedUseCase(testDispatcher))
        advanceUntilIdle()
        val before: List<VideoItem> = viewModel.state.value.videos
        val target = before.first { it.id == "2" }

        viewModel.onIntent(HomeIntent.LikeVideo("2"))

        val after = viewModel.state.value.videos
        assertEquals(before.map { it.id }, after.map { it.id })
        assertEquals(target.copy(likes = target.likes + 1), after.first { it.id == "2" })
    }

    @Test
    fun `liking a video before the feed loads is a no-op and does not block the load`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)

        viewModel.onIntent(HomeIntent.LikeVideo("1"))
        assertTrue(viewModel.state.value.videos.isEmpty())

        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
        assertEquals(getFeedUseCase().first { it.id == "1" }.likes, state.videos.first { it.id == "1" }.likes)
    }

    @Test
    fun `feed state mirrors the use case result exactly`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val expected = getFeedUseCase()
        assertEquals(expected, viewModel.state.value.videos)
        assertEquals(expected.size, expected.map { it.id }.distinct().size)
    }
}
