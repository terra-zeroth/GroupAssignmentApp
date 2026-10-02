package com.example.groupprojectapp.tasks

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    currentUserName: String,
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    Box {
                        IconButton(onClick = { sortMenuExpanded = true }) {
                            Icon(imageVector = Icons.Filled.Sort, contentDescription = "Sort tasks")
                        }
                        DropdownMenu(
                            expanded = sortMenuExpanded,
                            onDismissRequest = { sortMenuExpanded = false }
                        ) {
                            TaskSortOrder.values().forEach { order ->
                                DropdownMenuItem(
                                    text = { Text(order.label()) },
                                    onClick = {
                                        viewModel.onSortOrderSelected(order)
                                        sortMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Job allocation: filter the same to-do list by assignee instead
            // of a separate screen, matching the group's approved design.
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                item {
                    FilterChip(
                        selected = uiState.selectedAssigneeId == null,
                        onClick = { viewModel.onAssigneeFilterSelected(null) },
                        label = { Text("All") },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                items(uiState.members, key = { it.id }) { member ->
                    val label = if (member.name == currentUserName) "${member.name} (me)" else member.name
                    FilterChip(
                        selected = uiState.selectedAssigneeId == member.id,
                        onClick = { viewModel.onAssigneeFilterSelected(member.id) },
                        label = { Text(label) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            if (uiState.overdueCount > 0) {
                Surface(color = MaterialTheme.colorScheme.errorContainer) {
                    Text(
                        text = "${uiState.overdueCount} overdue",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            }

            if (uiState.tasks.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = "No tasks yet — tap + to add one.",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(uiState.tasks, key = { it.task.id }) { taskWithAssignee ->
                        TaskRow(
                            taskWithAssignee = taskWithAssignee,
                            onClick = { onTaskClick(taskWithAssignee.task.id) },
                            onToggleDone = { viewModel.toggleDone(taskWithAssignee.task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TaskRow(
    taskWithAssignee: TaskWithAssignee,
    onClick: () -> Unit,
    onToggleDone: () -> Unit
) {
    val task = taskWithAssignee.task
    val dueDate = remember(task.dueDateEpochDay) { LocalDate.ofEpochDay(task.dueDateEpochDay) }
    val isDone = task.status == TaskStatus.DONE

    ListItem(
        headlineContent = {
            Text(
                text = task.title,
                textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None
            )
        },
        supportingContent = {
            val assigneeText = taskWithAssignee.assignee?.name ?: "Unassigned"
            Text("$assigneeText · due ${dueDate.format(DateTimeFormatter.ofPattern("d MMM"))}")
        },
        leadingContent = {
            Checkbox(checked = isDone, onCheckedChange = { onToggleDone() })
        },
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    )
}

private fun TaskSortOrder.label(): String = when (this) {
    TaskSortOrder.DUE_DATE -> "Due date"
    TaskSortOrder.PRIORITY -> "Priority"
    TaskSortOrder.ASSIGNEE -> "Assignee"
}
