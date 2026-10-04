package com.example.myapplication.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.model.Task
import com.example.myapplication.domain.usecase.GetTasksUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TaskViewModel(
    private val getTasksUseCase: GetTasksUseCase = GetTasksUseCase()
) : ViewModel() {

    private val _state = MutableStateFlow(TaskUiState())
    val state: StateFlow<TaskUiState> = _state.asStateFlow()

    private var nextTaskNumber = 1

    init {
        loadTasks()
    }

    fun onIntent(intent: TaskIntent) {
        when (intent) {
            TaskIntent.LoadTasks -> loadTasks()
            is TaskIntent.CreateTask -> createTask(intent.title, intent.description)
            is TaskIntent.CompleteTask -> completeTask(intent.taskId)
            is TaskIntent.DeleteTask -> deleteTask(intent.taskId)
            is TaskIntent.SelectTask -> _state.update { it.copy(selectedTask = intent.task) }
            TaskIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun loadTasks() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val tasks = getTasksUseCase()
                _state.update { it.copy(tasks = tasks, isLoading = false) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to load tasks") }
            }
        }
    }

    private fun createTask(title: String, description: String) {
        if (title.isBlank()) {
            _state.update { it.copy(errorMessage = "Title cannot be empty") }
            return
        }
        val task = Task(
            id = "local-${nextTaskNumber++}",
            title = title.trim(),
            description = description.trim(),
            isCompleted = false,
            dueDate = "",
            priority = "Medium",
            createdAt = System.currentTimeMillis()
        )
        _state.update { it.copy(tasks = listOf(task) + it.tasks) }
    }

    private fun completeTask(taskId: String) {
        _state.update { current ->
            current.copy(
                tasks = current.tasks.map { if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it },
                selectedTask = current.selectedTask?.let {
                    if (it.id == taskId) it.copy(isCompleted = !it.isCompleted) else it
                }
            )
        }
    }

    private fun deleteTask(taskId: String) {
        _state.update { current ->
            current.copy(
                tasks = current.tasks.filter { it.id != taskId },
                selectedTask = current.selectedTask?.takeIf { it.id != taskId }
            )
        }
    }
}
