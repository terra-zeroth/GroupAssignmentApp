package com.example.groupprojectapp.tasks.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskPriority
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

/**
 * VIEW (MVVM):
 * the add/edit task form. Fields: title, description, start/due date
 * (date picker), priority (segmented buttons), assignee (dropdown of team
 * members), depends-on (dropdown of other tasks), plus a Save button.
 *
 * What it does NOT do: it holds no form data itself. Every field reads
 * from [TaskDetailUiState] and reports changes to [TaskDetailViewModel].
 * Validation and saving happen in the ViewModel.
 */

enum class DatePickerTarget{
    NONE,
    START_DATE,
    DUE_DATE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    viewModel: TaskDetailViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var activePicker by rememberSaveable { mutableStateOf(DatePickerTarget.NONE) }
    var assigneeMenuExpanded by remember { mutableStateOf(false) }
    var dependsOnMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (uiState.isNewTask) "New task" else "Edit task") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.title,
                onValueChange = viewModel::onTitleChange,
                label = { Text("Title") },
                isError = uiState.titleError,
                supportingText = { if (uiState.titleError) Text("Title is required") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.description,
                onValueChange = viewModel::onDescriptionChange,
                label = { Text("Description") },
                minLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text("Start date", style = MaterialTheme.typography.labelLarge)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.startDate.format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    isError = uiState.startDateError,
                    supportingText = {
                        if (uiState.startDateError)
                            Text("Start date can't be after the due date")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { activePicker = DatePickerTarget.START_DATE }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Due date", style = MaterialTheme.typography.labelLarge)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.dueDate.format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { activePicker = DatePickerTarget.DUE_DATE }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Priority", style = MaterialTheme.typography.labelLarge)
            val priorities = TaskPriority.values()
            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                priorities.forEachIndexed { index, priority ->
                    SegmentedButton(
                        selected = uiState.priority == priority,
                        onClick = { viewModel.onPriorityChange(priority) },
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = priorities.size)
                    ) {
                        Text(priority.name)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Assigned to (job allocation)", style = MaterialTheme.typography.labelLarge)
            val selectedAssigneeName = uiState.members
                .firstOrNull { it.id == uiState.assigneeId }
                ?.name ?: "Unassigned"

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedAssigneeName,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Assignee") },
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Choose assignee")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { assigneeMenuExpanded = true }
                )
                DropdownMenu(
                    expanded = assigneeMenuExpanded,
                    onDismissRequest = { assigneeMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Unassigned") },
                        onClick = {
                            viewModel.onAssigneeChange(null)
                            assigneeMenuExpanded = false
                        }
                    )
                    uiState.members.forEach { member ->
                        DropdownMenuItem(
                            text = { Text(member.name) },
                            onClick = {
                                viewModel.onAssigneeChange(member.id)
                                assigneeMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text("Depends on", style = MaterialTheme.typography.labelLarge)
            val selectedDependsOnTitle = uiState.availableTasks
                .firstOrNull { it.id == uiState.dependsOnTaskId }
                ?.title ?: "None"

            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedDependsOnTitle,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Depends on") },
                    trailingIcon = {
                        Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "Choose a task this depends on")
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { dependsOnMenuExpanded = true }
                )
                DropdownMenu(
                    expanded = dependsOnMenuExpanded,
                    onDismissRequest = { dependsOnMenuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("None") },
                        onClick = {
                            viewModel.onDependsOnChange(null)
                            dependsOnMenuExpanded = false
                        }
                    )
                    uiState.availableTasks.forEach { task ->
                        DropdownMenuItem(
                            text = { Text(task.title) },
                            onClick = {
                                viewModel.onDependsOnChange(task.id)
                                dependsOnMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { viewModel.saveTask(onSaved) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save task")
            }
        }
    }

    if (activePicker != DatePickerTarget.NONE) {
        val initialMillis = when (activePicker) {
            DatePickerTarget.START_DATE ->
                uiState.startDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

            DatePickerTarget.DUE_DATE ->
                uiState.dueDate.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()

            DatePickerTarget.NONE -> 0L
        }

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )
        DatePickerDialog(
            onDismissRequest = { activePicker = DatePickerTarget.NONE },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()

                        when (activePicker) {
                            DatePickerTarget.START_DATE -> viewModel.onStartDateChange(date)
                            DatePickerTarget.DUE_DATE -> viewModel.onDueDateChange(date)
                            DatePickerTarget.NONE -> Unit
                        }
                    }
                    activePicker = DatePickerTarget.NONE
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { activePicker = DatePickerTarget.NONE }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
