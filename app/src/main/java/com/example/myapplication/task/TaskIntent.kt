package com.example.myapplication.task

import com.example.myapplication.data.model.Task

sealed interface TaskIntent {
    data object LoadTasks : TaskIntent
    data class CreateTask(val title: String, val description: String) : TaskIntent
    data class CompleteTask(val taskId: String) : TaskIntent
    data class DeleteTask(val taskId: String) : TaskIntent
    data class SelectTask(val task: Task?) : TaskIntent
    data object DismissError : TaskIntent
}
