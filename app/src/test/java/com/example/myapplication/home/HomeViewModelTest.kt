package com.example.myapplication.home

import com.example.myapplication.data.model.VideoItem
import com.example.myapplication.domain.usecase.GetFeedUseCase
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
    fun `topics list is populated with distinct topics after load`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(state.videos.map { it.topic }.distinct(), state.topics)
        assertEquals(state.topics.size, state.topics.distinct().size)
    }

    @Test
    fun `selecting a topic filters videos to only that topic`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val topic = viewModel.state.value.topics.first()

        viewModel.onIntent(HomeIntent.SelectTopic(topic))

        val state = viewModel.state.value
        assertEquals(topic, state.selectedTopic)
        assertEquals(true, state.videos.isNotEmpty())
        state.videos.forEach { assertEquals(topic, it.topic) }
    }

    @Test
    fun `selecting null restores the full For You list`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val fullSize = viewModel.state.value.videos.size
        val topic = viewModel.state.value.topics.first()
        viewModel.onIntent(HomeIntent.SelectTopic(topic))

        viewModel.onIntent(HomeIntent.SelectTopic(null))

        val state = viewModel.state.value
        assertEquals(null, state.selectedTopic)
        assertEquals(fullSize, state.videos.size)
    }

    @Test
    fun `liking a video does not reorder the currently displayed list`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val orderBefore = viewModel.state.value.videos.map { it.id }
        val targetVideoId = orderBefore.last()

        viewModel.onIntent(HomeIntent.LikeVideo(targetVideoId))

        val orderAfter = viewModel.state.value.videos.map { it.id }
        assertEquals(orderBefore, orderAfter)
    }

    @Test
    fun `liking videos in one topic biases the For You ordering the next time it is selected`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = HomeViewModel(getFeedUseCase)
        advanceUntilIdle()

        val targetTopic = viewModel.state.value.videos.last().topic
        val targetVideoId = viewModel.state.value.videos.first { it.topic == targetTopic }.id

        viewModel.onIntent(HomeIntent.LikeVideo(targetVideoId))
        viewModel.onIntent(HomeIntent.SelectTopic(targetTopic))
        viewModel.onIntent(HomeIntent.SelectTopic(null))

        val state = viewModel.state.value
        assertEquals(targetTopic, state.videos.first().topic)
    }
}
