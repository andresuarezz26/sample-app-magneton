package com.example.myapplication.domain.usecase

import com.example.myapplication.data.model.Task
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

class GetTasksUseCase(
    private val ioDispatcher: CoroutineContext = Dispatchers.IO
) {

    suspend operator fun invoke(): List<Task> = withContext(ioDispatcher) {
        delay(500)
        listOf(
            Task(
                id = "1",
                title = "Design onboarding flow",
                description = "Sketch the three onboarding screens and share with the team.",
                isCompleted = false,
                dueDate = "2026-10-10",
                priority = "High",
                createdAt = 1_759_449_600_000
            ),
            Task(
                id = "2",
                title = "Write release notes",
                description = "Summarize the changes shipped in the latest build.",
                isCompleted = true,
                dueDate = "2026-10-05",
                priority = "Medium",
                createdAt = 1_759_363_200_000
            ),
            Task(
                id = "3",
                title = "Fix upload crash",
                description = "Investigate the crash when selecting more than ten photos.",
                isCompleted = false,
                dueDate = "2026-10-07",
                priority = "High",
                createdAt = 1_759_276_800_000
            ),
            Task(
                id = "4",
                title = "Update app icon",
                description = "Replace the placeholder launcher icon with the new brand asset.",
                isCompleted = false,
                dueDate = "2026-10-20",
                priority = "Low",
                createdAt = 1_759_190_400_000
            ),
            Task(
                id = "5",
                title = "Review pull requests",
                description = "Go through the open pull requests assigned to me.",
                isCompleted = true,
                dueDate = "2026-10-04",
                priority = "Medium",
                createdAt = 1_759_104_000_000
            ),
            Task(
                id = "6",
                title = "Plan next sprint",
                description = "Prioritize the backlog and estimate the top items.",
                isCompleted = false,
                dueDate = "2026-10-15",
                priority = "Low",
                createdAt = 1_759_017_600_000
            )
        )
    }
}
