package com.example.myapplication.task

import com.example.myapplication.data.model.Task

data class TaskUiState(
    val tasks: List<Task> = emptyList(),
    val isLoading: Boolean = false,
    val selectedTask: Task? = null,
    val errorMessage: String? = null
)
