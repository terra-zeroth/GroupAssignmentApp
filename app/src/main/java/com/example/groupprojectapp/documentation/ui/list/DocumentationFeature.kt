package com.example.groupprojectapp.documentation.ui.list

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

/**
 * WRAPPER (not previewable):
 * creates DocumentationViewModel with the
 * repositories from TaskContainer, collects its state and passes it to
 * [DocumentationContent]. All UI lives in DocumentationContent, which is
 * previewable with fake state.
 *
 * Back:
 * the TopAppBar arrow and the system Back gesture (BackHandler) both
 * call onBackClick, so Back never exits the app (R1).
 */

@Composable
fun DocumentationFeature(
    taskId: Long?,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) }

    // remember(taskId): build a new factory when a different task is opened
    val factory = remember(taskId) {

        // MainActivity owns this ViewModel, so without a key, opening task 8 after
        // task 5 would hand back task 5's ViewModel. The key gives each task (and
        // "all") its own instance, so the list never shows a stale filter.
        viewModelFactory {
            initializer {
                DocumentationViewModel(
                    repository = container.documentationRepository,
                    taskRepository = container.taskRepository,
                    taskId = taskId
                )
            }
        }
    }

    val viewModel: DocumentationViewModel = viewModel(
        key = "documentation-${taskId ?: "all"}",
        factory = factory
    )
    val uiState by viewModel.uiState.collectAsState()

    DocumentationContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onEntryClick = onEntryClick,
        modifier = modifier
    )
}