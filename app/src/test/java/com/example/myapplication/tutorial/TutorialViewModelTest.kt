package com.example.myapplication.tutorial

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class TutorialViewModelTest {

    private lateinit var viewModel: TutorialViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = TutorialViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun initialState_hasCorrectDefaults() = runTest {
        val state = viewModel.state.first()
        assertEquals(0, state.currentPage)
        assertEquals(4, state.totalPages)
    }

    @Test
    fun nextPage_incrementsCurrentPage() = runTest {
        viewModel.onIntent(TutorialIntent.NextPage)
        val state = viewModel.state.first()
        assertEquals(1, state.currentPage)
    }

    @Test
    fun nextPage_doesNotExceedTotalPages() = runTest {
        repeat(5) {
            viewModel.onIntent(TutorialIntent.NextPage)
        }
        val state = viewModel.state.first()
        assertEquals(3, state.currentPage)
    }

    @Test
    fun previousPage_decrementsCurrentPage() = runTest {
        viewModel.onIntent(TutorialIntent.NextPage)
        viewModel.onIntent(TutorialIntent.PreviousPage)
        val state = viewModel.state.first()
        assertEquals(0, state.currentPage)
    }

    @Test
    fun previousPage_doesNotGoNegative() = runTest {
        viewModel.onIntent(TutorialIntent.PreviousPage)
        val state = viewModel.state.first()
        assertEquals(0, state.currentPage)
    }

    @Test
    fun skipTutorial_executesWithoutError() = runTest {
        viewModel.onIntent(TutorialIntent.SkipTutorial)
        val state = viewModel.state.first()
        assertEquals(0, state.currentPage)
    }

    @Test
    fun completeTutorial_executesWithoutError() = runTest {
        viewModel.onIntent(TutorialIntent.CompleteTutorial)
        val state = viewModel.state.first()
        assertEquals(0, state.currentPage)
    }
}
