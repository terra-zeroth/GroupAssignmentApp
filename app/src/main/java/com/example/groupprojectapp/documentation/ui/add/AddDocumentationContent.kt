package com.example.groupprojectapp.documentation.ui.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentationContent(
    uiState: AddDocumentationUiState,
    taskOptions: List<TaskOption>,
    onTaskSelected: (Long) -> Unit,
    onNoteChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("New Documentation Entry") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Task Picker Section
            Text(text = "Select Task *", style = MaterialTheme.typography.titleSmall)

            if (taskOptions.isEmpty()) {
                Text(
                    text = "No tasks available. Please create a task first.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                // Simple dropdown or radio group representation for task selection
                taskOptions.forEach { task ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = task.title, style = MaterialTheme.typography.bodyLarge)
                        RadioButton(
                            selected = uiState.selectedTaskId == task.id,
                            onClick = { onTaskSelected(task.id) }
                        )
                    }
                }

                if (uiState.taskError != null) {
                    Text(
                        text = uiState.taskError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Display
            OutlinedTextField(
                value = uiState.date.toString(),
                onValueChange = {},
                readOnly = true,
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Note Input Field
            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChanged,
                label = { Text("Note *") },
                isError = uiState.noteError != null,
                supportingText = {
                    if (uiState.noteError != null) {
                        Text(text = uiState.noteError!!)
                    }
                },
                minLines = 4,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Save Button
            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Entry")
            }


        }
    }
}

// REVIEWS =======================

// ==========================================
// PREVIEWS
// ==========================================

@Preview(name = "Normal Form", showBackground = true)
@Preview(name = "Dark Mode", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun AddDocumentationContentPreview() {
    GroupProjectAppTheme {
        AddDocumentationContent(
            uiState = AddDocumentationUiState(
                selectedTaskId = 1L,
                note = "Finished setting up Room database and DAOs."
            ),
            taskOptions = listOf(
                TaskOption(1L, "Design Architecture"),
                TaskOption(2L, "Implement UI Components")
            ),
            onTaskSelected = {},
            onNoteChanged = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "Error State", showBackground = true)
@Composable
fun AddDocumentationContentErrorPreview() {
    GroupProjectAppTheme {
        AddDocumentationContent(
            uiState = AddDocumentationUiState(
                selectedTaskId = null,
                note = "",
                taskError = "Please select a task",
                noteError = "Note cannot be blank"
            ),
            taskOptions = listOf(
                TaskOption(1L, "Design Architecture")
            ),
            onTaskSelected = {},
            onNoteChanged = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}

@Preview(name = "No Tasks State", showBackground = true)
@Composable
fun AddDocumentationContentEmptyTasksPreview() {
    GroupProjectAppTheme {
        AddDocumentationContent(
            uiState = AddDocumentationUiState(),
            taskOptions = emptyList(),
            onTaskSelected = {},
            onNoteChanged = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}