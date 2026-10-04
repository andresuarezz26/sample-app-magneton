package com.example.myapplication.task

import com.example.myapplication.domain.usecase.GetTasksUseCase
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
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class TaskViewModelTest {

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel() = TaskViewModel(GetTasksUseCase(testDispatcher))

    @Test
    fun `init loads tasks and toggles isLoading`() = runTest(testDispatcher) {
        val viewModel = createViewModel()

        testDispatcher.scheduler.runCurrent()
        assertTrue(viewModel.state.value.isLoading)

        advanceUntilIdle()

        assertFalse(viewModel.state.value.isLoading)
        assertEquals(6, viewModel.state.value.tasks.size)
    }

    @Test
    fun `creating a task prepends it to the list`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TaskIntent.CreateTask("New", "Desc"))

        val tasks = viewModel.state.value.tasks
        assertEquals(7, tasks.size)
        assertEquals("New", tasks.first().title)
        assertFalse(tasks.first().isCompleted)
    }

    @Test
    fun `creating a task with a blank title sets an error that can be dismissed`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TaskIntent.CreateTask("  ", "Desc"))
        assertEquals(6, viewModel.state.value.tasks.size)
        assertEquals("Title cannot be empty", viewModel.state.value.errorMessage)

        viewModel.onIntent(TaskIntent.DismissError)
        assertNull(viewModel.state.value.errorMessage)
    }

    @Test
    fun `completing a task toggles its completion`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()

        viewModel.onIntent(TaskIntent.CompleteTask("1"))

        assertTrue(viewModel.state.value.tasks.first { it.id == "1" }.isCompleted)
    }

    @Test
    fun `deleting a task removes it and clears matching selection`() = runTest(testDispatcher) {
        val viewModel = createViewModel()
        advanceUntilIdle()
        val task = viewModel.state.value.tasks.first { it.id == "1" }
        viewModel.onIntent(TaskIntent.SelectTask(task))
        assertEquals(task, viewModel.state.value.selectedTask)

        viewModel.onIntent(TaskIntent.DeleteTask("1"))

        assertEquals(5, viewModel.state.value.tasks.size)
        assertNull(viewModel.state.value.selectedTask)
    }
}
