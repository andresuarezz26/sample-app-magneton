package com.example.myapplication.discover

import com.example.myapplication.domain.usecase.GetFeedUseCase
import com.example.myapplication.domain.usecase.GetTopicsUseCase
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
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DiscoverViewModelTest {

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
    fun `initial load populates available topics and all videos`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getFeedUseCase, testDispatcher)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals(7, state.availableTopics.size)
        assertEquals(6, state.filteredVideos.size)
        assertNull(state.selectedTopic)
        assertFalse(state.isLoading)
    }

    @Test
    fun `selecting a topic filters videos by that topic`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getFeedUseCase, testDispatcher)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectTopic("physics"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("physics", state.selectedTopic)
        assertEquals(2, state.filteredVideos.size)
        state.filteredVideos.forEach { video ->
            assert(video.topics.contains("physics"))
        }
    }

    @Test
    fun `clearing selection shows all videos again`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getFeedUseCase, testDispatcher)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectTopic("physics"))
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.ClearSelection)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertNull(state.selectedTopic)
        assertEquals(6, state.filteredVideos.size)
    }

    @Test
    fun `selecting a topic with no videos results in empty list`() = runTest(testDispatcher) {
        val getFeedUseCase = GetFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getFeedUseCase, testDispatcher)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectTopic("nonexistent"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertEquals("nonexistent", state.selectedTopic)
        assertEquals(0, state.filteredVideos.size)
    }
}
