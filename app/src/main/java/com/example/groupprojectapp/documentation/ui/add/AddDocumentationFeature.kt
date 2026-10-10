package com.example.groupprojectapp.documentation.ui.add

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
    modifier: Modifier = Modifier,
    currentUsername: String
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

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                e.printStackTrace()
            }
            viewModel.onImagePicked(uri.toString())
        }
    }

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
        onDateSelected = { viewModel.onDateChange(it) },
        onSaveClick = {
            viewModel.saveEntry(currentUsername) { // Passes the fresh logged-in username here!
                onSaved()
            }
        },
        onBackClick = handleBack,
        onAttachImageClick = {
            photoPickerLauncher.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        },
        onRemoveImageClick = { viewModel.onImageRemoved() },
        modifier = modifier
    )
}