package com.example.myapplication.data.model

data class Task(
    val id: String,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val dueDate: String,
    val priority: String,
    val createdAt: Long
)
