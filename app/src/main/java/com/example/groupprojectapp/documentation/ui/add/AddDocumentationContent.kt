package com.example.groupprojectapp.documentation.ui.add

import android.content.res.Configuration
import androidx.compose.foundation.clickable
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
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDocumentationContent(
    uiState: AddDocumentationUiState,
    taskOptions: List<TaskOption>,
    onTaskSelected: (Long) -> Unit,
    onNoteChanged: (String) -> Unit,
    onDateSelected: (LocalDate) -> Unit,
    onSaveClick: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDatePicker by remember { mutableStateOf(false) }

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

                uiState.taskError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date Display Field
            Text("Date", style = MaterialTheme.typography.labelLarge)
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = uiState.date.format(DateTimeFormatter.ofPattern("d MMM yyyy")),
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clickable { showDatePicker = true }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Note Input Field
            OutlinedTextField(
                value = uiState.note,
                onValueChange = onNoteChanged,
                label = { Text("Note *") },
                isError = uiState.noteError != null,
                supportingText = {
                    uiState.noteError?.let { error ->
                        Text(text = error)
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

    // Date Picker Dialog Overlay
    if (showDatePicker) {
        val initialMillis = uiState.date
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = initialMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val selectedDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

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
            onDateSelected = {},
            onSaveClick = {},
            onBackClick = {}
        )
    }
}