package com.example.myapplication.discover

import com.example.myapplication.domain.usecase.GetDiscoverFeedUseCase
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
    fun `initial load populates feed with papers and isLoading is false`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        val successState = state as DiscoverUiState.Success
        assertEquals(4, successState.papers.size)
        assertEquals("All", successState.selectedCategory)
        assertTrue(successState.hasMore)
    }

    @Test
    fun `selecting category filters papers by field`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory("Physics"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        val successState = state as DiscoverUiState.Success
        assertEquals("Physics", successState.selectedCategory)
        successState.papers.forEach { paper ->
            assertEquals("Physics", paper.field)
        }
    }

    @Test
    fun `selecting All category shows all papers`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory("All"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        val successState = state as DiscoverUiState.Success
        assertEquals("All", successState.selectedCategory)
        assertEquals(4, successState.papers.size)
    }

    @Test
    fun `loading next page appends papers to existing list`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        var state = viewModel.state.value as DiscoverUiState.Success
        val initialPapers = state.papers.toList()
        assertEquals(4, initialPapers.size)

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(8, state.papers.size)
        assertEquals(initialPapers, state.papers.take(4))
    }

    @Test
    fun `loading next page when no more pages does nothing`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        var state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(4, state.papers.size)
        assertTrue(state.hasMore)

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        state = viewModel.state.value as DiscoverUiState.Success
        assertFalse(state.hasMore)
        val paperCountAtEnd = state.papers.size

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(paperCountAtEnd, state.papers.size)
    }

    @Test
    fun `pagination resets when changing category`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        var state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(8, state.papers.size)

        viewModel.onIntent(DiscoverIntent.SelectCategory("Biology"))
        advanceUntilIdle()

        state = viewModel.state.value as DiscoverUiState.Success
        assertEquals("Biology", state.selectedCategory)
        assertEquals(4, state.papers.size)
    }
}
