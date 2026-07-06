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
import org.junit.Assert.fail
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
    fun `initial load populates first page as Success`() = runTest(testDispatcher) {
        val viewModel = DiscoverViewModel(GetDiscoverFeedUseCase(testDispatcher))
        advanceUntilIdle()

        val state = viewModel.state.value
        if (state !is DiscoverUiState.Success) fail("Expected Success but was $state")
        state as DiscoverUiState.Success
        assertEquals(10, state.papers.size)
        assertTrue(state.categories.isNotEmpty())
        assertEquals(null, state.selectedCategory)
        assertFalse(state.endReached)
    }

    @Test
    fun `loadNextPage appends and eventually sets endReached`() = runTest(testDispatcher) {
        val viewModel = DiscoverViewModel(GetDiscoverFeedUseCase(testDispatcher))
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        var state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(20, state.papers.size)
        assertFalse(state.endReached)

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        state = viewModel.state.value as DiscoverUiState.Success
        assertEquals(30, state.papers.size)
        assertTrue(state.endReached)
    }

    @Test
    fun `selectCategory resets pagination and filters`() = runTest(testDispatcher) {
        val viewModel = DiscoverViewModel(GetDiscoverFeedUseCase(testDispatcher))
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory("Physics"))
        advanceUntilIdle()

        val state = viewModel.state.value as DiscoverUiState.Success
        assertEquals("Physics", state.selectedCategory)
        assertTrue(state.papers.isNotEmpty())
        assertTrue(state.papers.all { it.field == "Physics" })
        assertTrue(state.endReached)
    }

    @Test
    fun `empty mock result set surfaces Success with empty list rather than Error`() = runTest(testDispatcher) {
        val viewModel = DiscoverViewModel(GetDiscoverFeedUseCase(testDispatcher))
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory("NonExistentField"))
        advanceUntilIdle()

        val state = viewModel.state.value
        if (state !is DiscoverUiState.Success) fail("Expected Success but was $state")
        state as DiscoverUiState.Success
        assertTrue(state.papers.isEmpty())
        assertTrue(state.endReached)
    }
}
