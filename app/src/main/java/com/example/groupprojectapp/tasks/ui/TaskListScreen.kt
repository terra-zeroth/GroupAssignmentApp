package com.example.groupprojectapp.tasks.ui

import com.example.groupprojectapp.tasks.data.DeadlineCalculations

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskSortOrder
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.tasks.data.TaskWithAssignee
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * VIEW (MVVM): the to-do list screen.
 *
 * What it shows: a top bar with a back arrow, a sort menu, a row of filter
 * chips (one per team member, for job allocation), an "overdue" banner and
 * the list of tasks. A + button opens the add-task screen.
 *
 * What it does NOT do: no sorting, filtering or database work happens here.
 * It collects [TaskListUiState] from [TaskListViewModel] and only draws it.
 * User actions (sort chosen, chip tapped, checkbox ticked) are passed back to
 * the ViewModel and navigation clicks are passed up through the lambdas.
 *
 * Colours (matching Timeline and Documentation): the top bar uses
 * tertiaryContainer (periwinkle / dark blue) with a navy line underneath;
 * the + button is periwinkle with a navy border; filter chips have a navy
 * border and white text when selected; the overdue banner uses the theme's
 * error colour (red with white text).
 *
 * Local UI-only state: [sortMenuExpanded] (is the dropdown open?) stays here
 * because it is not app data.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskListViewModel,
    currentUserName: String,
    onTaskClick: (Long) -> Unit,
    onAddTaskClick: (Long?) -> Unit,
    onBackToHome: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var sortMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Top bar with a navy line underneath, matching Timeline and Documentation
            Column {
                TopAppBar(
                    title = { Text("Tasks") },
                    // Top bar uses tertiaryContainer from the theme (periwinkle / dark blue)
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackToHome) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Home"
                            )
                        }
                    },
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

                // Navy border line along the bottom of the top bar
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        floatingActionButton = {
            // Periwinkle fill, navy icon and navy border, matching Timeline and Documentation
            FloatingActionButton(
                onClick = { onAddTaskClick(uiState.selectedAssigneeId) },
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = FloatingActionButtonDefaults.shape
                )
            ) {
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
                    TaskFilterChip(
                        label = "All",
                        selected = uiState.selectedAssigneeId == null,
                        onClick = { viewModel.onAssigneeFilterSelected(null) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
                items(uiState.members, key = { it.id }) { member ->
                    val label = if (member.name == currentUserName) "${member.name} (me)" else member.name
                    TaskFilterChip(
                        label = label,
                        selected = uiState.selectedAssigneeId == member.id,
                        onClick = { viewModel.onAssigneeFilterSelected(member.id) },
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }

            if (uiState.overdueCount > 0) {
                // Same red as overdue bars and documentation cards, with white text for contrast
                Surface(color = MaterialTheme.colorScheme.error) {
                    Text(
                        text = "${uiState.overdueCount} overdue",
                        color = MaterialTheme.colorScheme.onError,
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

/** One filter chip in the app's shared style: navy border, white text when selected. */
@Composable
private fun TaskFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.outline,
            borderWidth = 1.5.dp,
            selectedBorderWidth = 1.5.dp
        ),
        modifier = modifier
    )
}


/** One row in the list: checkbox, title (struck through if done), assignee and due date. */
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
            val deadlineText = DeadlineCalculations.deadlineLabel(task.dueDateEpochDay)
            Text("$assigneeText · due ${dueDate.format(DateTimeFormatter.ofPattern("d MMM"))} · $deadlineText")
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