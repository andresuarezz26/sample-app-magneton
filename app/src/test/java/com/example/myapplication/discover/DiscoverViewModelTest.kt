package com.example.myapplication.discover

import com.example.myapplication.data.model.ScientificField
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
    fun `initial load populates feed with first page of papers`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        state as DiscoverUiState.Success
        assertEquals(3, state.papers.size)
        assertFalse(state.isPaginating)
        assertFalse(state.endReached)
    }

    @Test
    fun `filtering by category resets pagination and shows filtered results`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory(ScientificField.PHYSICS))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        state as DiscoverUiState.Success
        assertEquals(ScientificField.PHYSICS, state.selectedCategory)
        assertTrue(state.papers.all { it.field == ScientificField.PHYSICS })
    }

    @Test
    fun `loading next page appends papers to feed`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        val firstPageState = viewModel.state.value
        assertTrue(firstPageState is DiscoverUiState.Success)
        firstPageState as DiscoverUiState.Success
        val firstPageSize = firstPageState.papers.size

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        val secondPageState = viewModel.state.value
        assertTrue(secondPageState is DiscoverUiState.Success)
        secondPageState as DiscoverUiState.Success
        assertTrue(secondPageState.papers.size > firstPageSize)
    }

    @Test
    fun `pagination ends when no more papers available`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()
        viewModel.onIntent(DiscoverIntent.LoadNextPage)
        advanceUntilIdle()

        val finalState = viewModel.state.value
        assertTrue(finalState is DiscoverUiState.Success)
        finalState as DiscoverUiState.Success
        assertTrue(finalState.endReached)
    }

    @Test
    fun `filtering by category loads papers for that category`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory(ScientificField.BIOLOGY))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertTrue(state is DiscoverUiState.Success)
        state as DiscoverUiState.Success
        assertTrue(state.papers.isNotEmpty())
        assertTrue(state.papers.all { it.field == ScientificField.BIOLOGY })
    }

    @Test
    fun `clearing category filter shows all papers`() = runTest(testDispatcher) {
        val getDiscoverFeedUseCase = GetDiscoverFeedUseCase(testDispatcher)
        val viewModel = DiscoverViewModel(getDiscoverFeedUseCase)
        advanceUntilIdle()

        viewModel.onIntent(DiscoverIntent.SelectCategory(ScientificField.BIOLOGY))
        advanceUntilIdle()
        val filteredState = viewModel.state.value
        assertTrue(filteredState is DiscoverUiState.Success)
        filteredState as DiscoverUiState.Success
        val filteredCount = filteredState.papers.size

        viewModel.onIntent(DiscoverIntent.SelectCategory(null))
        advanceUntilIdle()
        val allState = viewModel.state.value
        assertTrue(allState is DiscoverUiState.Success)
        allState as DiscoverUiState.Success
        assertTrue(allState.papers.size >= filteredCount)
        assertFalse(allState.papers.all { it.field == ScientificField.BIOLOGY })
    }
}
