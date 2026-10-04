package com.example.myapplication.task

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.data.model.Task
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.theme.PriorityHigh
import com.example.myapplication.ui.theme.PriorityLow
import com.example.myapplication.ui.theme.PriorityMedium

@Composable
fun TaskScreen(viewModel: TaskViewModel = viewModel(), onBack: () -> Unit = {}) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    TaskContent(state = state, onIntent = viewModel::onIntent, onBack = onBack)
}

@Composable
private fun TaskContent(
    state: TaskUiState,
    onIntent: (TaskIntent) -> Unit,
    onBack: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "←",
                    fontSize = 22.sp,
                    modifier = Modifier.clickable { onBack() }
                )
                Text(
                    text = "Tasks",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 16.dp)
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                modifier = Modifier.navigationBarsPadding()
            ) {
                Text(text = "+", fontSize = 24.sp)
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            state.errorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clickable { onIntent(TaskIntent.DismissError) }
                )
            }

            Box(modifier = Modifier.fillMaxSize()) {
                when {
                    state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                    state.tasks.isEmpty() -> Text(
                        text = "No tasks yet",
                        modifier = Modifier.align(Alignment.Center)
                    )
                    else -> LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(state.tasks, key = { it.id }) { task ->
                            TaskItem(
                                task = task,
                                onClick = { onIntent(TaskIntent.SelectTask(task)) },
                                onComplete = { onIntent(TaskIntent.CompleteTask(task.id)) },
                                onDelete = { onIntent(TaskIntent.DeleteTask(task.id)) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        CreateTaskDialog(
            onDismiss = { showCreateDialog = false },
            onCreate = { title, description ->
                onIntent(TaskIntent.CreateTask(title, description))
                showCreateDialog = false
            }
        )
    }

    state.selectedTask?.let { task ->
        AlertDialog(
            onDismissRequest = { onIntent(TaskIntent.SelectTask(null)) },
            title = { Text(text = task.title) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = task.description)
                    Text(text = "Priority: ${task.priority}", fontSize = 13.sp)
                    if (task.dueDate.isNotEmpty()) {
                        Text(text = "Due: ${task.dueDate}", fontSize = 13.sp)
                    }
                    Text(text = if (task.isCompleted) "Completed" else "Pending", fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { onIntent(TaskIntent.SelectTask(null)) }) { Text("Close") }
            }
        )
    }
}

@Composable
private fun CreateTaskDialog(onDismiss: () -> Unit, onCreate: (String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("New task") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") })
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onCreate(title, description) }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun TaskItem(
    task: Task,
    onClick: () -> Unit,
    onComplete: () -> Unit,
    onDelete: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = task.isCompleted, onCheckedChange = { onComplete() })
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                )
                if (task.description.isNotEmpty()) {
                    Text(text = task.description, fontSize = 13.sp, maxLines = 2)
                }
                PriorityBadge(priority = task.priority)
            }
            TextButton(onClick = onDelete) { Text("✕") }
        }
    }
}

@Composable
private fun PriorityBadge(priority: String) {
    val color = when (priority) {
        "High" -> PriorityHigh
        "Medium" -> PriorityMedium
        else -> PriorityLow
    }
    Text(
        text = priority,
        color = Color.Black,
        fontSize = 11.sp,
        modifier = Modifier
            .padding(top = 4.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(color)
            .padding(horizontal = 8.dp, vertical = 2.dp)
    )
}

private val previewTasks = listOf(
    Task("1", "Design onboarding flow", "Sketch the three screens.", false, "2026-10-10", "High", 0L),
    Task("2", "Write release notes", "Summarize the latest build.", true, "2026-10-05", "Medium", 0L),
    Task("3", "Update app icon", "Use the new brand asset.", false, "2026-10-20", "Low", 0L)
)

@Preview(showBackground = true)
@Composable
private fun TaskScreenEmptyPreview() {
    MyApplicationTheme {
        TaskContent(state = TaskUiState(), onIntent = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskScreenWithTasksPreview() {
    MyApplicationTheme {
        TaskContent(state = TaskUiState(tasks = previewTasks), onIntent = {}, onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun TaskScreenLoadingPreview() {
    MyApplicationTheme {
        TaskContent(state = TaskUiState(isLoading = true), onIntent = {}, onBack = {})
    }
}
