package com.example.groupprojectapp.documentation.ui.add

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groupprojectapp.tasks.TaskContainer

@Composable
fun AddDocumentationFeature(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) }

    val factory = remember {
        viewModelFactory {
            initializer {
                AddDocumentationViewModel(
                    documentationRepository = container.documentationRepository,
                    taskRepository = container.taskRepository
                )
            }
        }
    }

    val viewModel: AddDocumentationViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()
    val taskOptions by viewModel.taskOptions.collectAsState()

    val handleBack: () -> Unit = {
        viewModel.resetForm()
        onBackClick()
    }

    BackHandler(onBack = handleBack)

    AddDocumentationContent(
        uiState = uiState,
        taskOptions = taskOptions,
        onTaskSelected = { viewModel.onTaskSelected(it) },
        onNoteChanged = { viewModel.onNoteChanged(it) },
        onSaveClick = {
            viewModel.saveEntry {
                onSaved() // Navigate back upon successful save
            }
        },
        onBackClick = handleBack,
        modifier = modifier
    )
}