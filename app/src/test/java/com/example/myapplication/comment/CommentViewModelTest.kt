package com.example.myapplication.comment

import com.example.myapplication.domain.usecase.GetCommentsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CommentViewModelTest {

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
    fun `initial state is loading with no comments`() = runTest(testDispatcher) {
        val getCommentsUseCase = GetCommentsUseCase(testDispatcher)
        val viewModel = CommentViewModel(getCommentsUseCase)

        val state = viewModel.state.value
        assertTrue(state.isLoading)
        assertTrue(state.comments.isEmpty())
    }

    @Test
    fun `loading comments populates list and clears isLoading`() = runTest(testDispatcher) {
        val getCommentsUseCase = GetCommentsUseCase(testDispatcher)
        val viewModel = CommentViewModel(getCommentsUseCase)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.comments.isNotEmpty())
    }
}
