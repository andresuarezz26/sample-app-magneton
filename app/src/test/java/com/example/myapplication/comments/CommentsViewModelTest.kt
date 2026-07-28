package com.example.myapplication.comments

import com.example.myapplication.domain.usecase.GetCommentsUseCase
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
class CommentsViewModelTest {

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
    fun `initial state is empty and not loading`() = runTest(testDispatcher) {
        val viewModel = CommentsViewModel(GetCommentsUseCase(testDispatcher))

        val state = viewModel.state.value
        assertTrue(state.comments.isEmpty())
        assertFalse(state.isLoading)
    }

    @Test
    fun `loading comments populates comments and clears isLoading`() = runTest(testDispatcher) {
        val viewModel = CommentsViewModel(GetCommentsUseCase(testDispatcher))

        viewModel.onIntent(CommentsIntent.LoadComments("1"))
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertTrue(state.comments.isNotEmpty())
    }

    @Test
    fun `liking a comment increments only that comment likes by 1`() = runTest(testDispatcher) {
        val viewModel = CommentsViewModel(GetCommentsUseCase(testDispatcher))
        viewModel.onIntent(CommentsIntent.LoadComments("1"))
        advanceUntilIdle()

        val commentsBefore = viewModel.state.value.comments.toList()
        val targetId = commentsBefore.first().id
        val initialLikes = commentsBefore.first().likes

        viewModel.onIntent(CommentsIntent.LikeComment(targetId))

        val commentsAfter = viewModel.state.value.comments
        assertEquals(initialLikes + 1, commentsAfter.first { it.id == targetId }.likes)
        commentsAfter.filter { it.id != targetId }.forEach { comment ->
            val original = commentsBefore.first { it.id == comment.id }
            assertEquals(original.likes, comment.likes)
        }
    }
}
