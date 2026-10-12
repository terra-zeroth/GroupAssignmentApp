package com.example.groupprojectapp.documentation.ui.add

import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groupprojectapp.tasks.TaskContainer

/**
 * WRAPPER (not previewable):
 * creates AddDocumentationViewModel, collects
 * its state and passes it to [AddDocumentationContent].
 * It also holds the things that need a real device, so the
 * Content stays previewable:
 * the photo picker launcher (A4) and loading an entry to edit.
 *
 * entryId: null = new entry; an id = load that entry for editing.
 * Back (arrow and system gesture) resets the form first, because
 * MainActivity owns the ViewModel and would otherwise keep old values.
 */

@Composable
fun AddDocumentationFeature(
    entryId: Long?,
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier,
    currentUsername: String,
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

    // Runs when the form opens (and again only if entryId changes)
    // loadEntry skips an entry that's already loaded, so rotation is safe
    LaunchedEffect(entryId) {
        entryId?.let { viewModel.loadEntry(it) }
    }

    // Android's photo picker (no storage permission needed):
    // uri is null if the user cancels, so nothing happens and nothing crashes (R6).
    // takePersistableUriPermission keeps read access after the app restarts;
    // some older devices can't grant it, so a SecurityException is caught
    // instead of crashing (the image still shows until restart).
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
            viewModel.saveEntry(currentUsername) {
                // Username is read at click time, so it's always the current user
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