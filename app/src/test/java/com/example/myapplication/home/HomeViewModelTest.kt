package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
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
    fun `isLoading toggles true during an in-flight load then false on completion`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val observedLoadingStates = mutableListOf<Boolean>()
        val collectJob = launch { viewModel.state.collect { observedLoadingStates.add(it.isLoading) } }

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()
        collectJob.cancel()

        assertTrue(observedLoadingStates.contains(true))
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `dispatching LoadFeed again re-fetches and overwrites prior local mutations`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetId = "1"
        val originalLikes = viewModel.state.value.videos.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        assertEquals(originalLikes + 1, viewModel.state.value.videos.first { it.id == targetId }.likes)

        viewModel.onIntent(HomeIntent.LoadFeed)
        advanceUntilIdle()

        assertEquals(originalLikes, viewModel.state.value.videos.first { it.id == targetId }.likes)
    }

    @Test
    fun `liking the same video multiple times accumulates correctly`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetId = "1"
        val initialLikes = viewModel.state.value.videos.first { it.id == targetId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        viewModel.onIntent(HomeIntent.LikeVideo(targetId))
        viewModel.onIntent(HomeIntent.LikeVideo(targetId))

        assertEquals(initialLikes + 3, viewModel.state.value.videos.first { it.id == targetId }.likes)
    }

    @Test
    fun `liking a video before the initial load resolves is a safe no-op`() = runTest(testDispatcher) {
        val referenceViewModel = HomeViewModel(GetFeedUseCase(testDispatcher))
        advanceUntilIdle()
        val expectedLikes = referenceViewModel.state.value.videos.first { it.id == "1" }.likes

        val viewModel = HomeViewModel(GetFeedUseCase(testDispatcher))

        viewModel.onIntent(HomeIntent.LikeVideo("1"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(6, state.videos.size)
        assertFalse(state.isLoading)
        assertEquals(expectedLikes, state.videos.first { it.id == "1" }.likes)
    }

    @Test
    fun `liking works correctly when applied to more than one distinct video id in sequence`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val videosBefore = viewModel.state.value.videos.toList()
        val firstId = "1"
        val secondId = "2"
        val firstInitialLikes = videosBefore.first { it.id == firstId }.likes
        val secondInitialLikes = videosBefore.first { it.id == secondId }.likes

        viewModel.onIntent(HomeIntent.LikeVideo(firstId))
        viewModel.onIntent(HomeIntent.LikeVideo(secondId))

        val videosAfter = viewModel.state.value.videos
        assertEquals(firstInitialLikes + 1, videosAfter.first { it.id == firstId }.likes)
        assertEquals(secondInitialLikes + 1, videosAfter.first { it.id == secondId }.likes)
        videosAfter.filter { it.id != firstId && it.id != secondId }.forEach { video ->
            val original = videosBefore.first { it.id == video.id }
            assertEquals(original.likes, video.likes)
        }
    }
}
