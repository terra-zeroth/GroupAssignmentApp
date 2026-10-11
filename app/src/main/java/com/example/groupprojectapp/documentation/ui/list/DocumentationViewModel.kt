package com.example.groupprojectapp.documentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.documentation.data.DocumentationEntryWithTask
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import com.example.groupprojectapp.tasks.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class DocumentationViewModel(
    repository: DocumentationRepository,
    taskRepository: TaskRepository,
    private val taskId: Long? = null
) : ViewModel() {

    // pick the query based on whether filtering
    private val entriesFlow =
        if (taskId == null) repository.allEntriesWithTask
        else repository.getEntriesForTask(taskId)

    val uiState: StateFlow<DocumentationUiState> =
        // allow the viewModel to have both the entry flow and task flow
        // so the Top bar can show the task's title even without entries yet
        combine(entriesFlow, taskRepository.allTasks) { entries, tasks ->

            // Look up the task title by ID (null when not filtering)
            val title = taskId?.let { id ->
                tasks.find { it.task.id == id }?.task?.title
            }

            DocumentationUiState(
                isLoading = false,
                entries = entries.map { it.toUiItem() },
                filterTaskTitle = title
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DocumentationUiState()
        )

    private fun DocumentationEntryWithTask.toUiItem(): DocumentationEntryItem {
        return DocumentationEntryItem(
            id = id,
            taskTitle = taskTitle,
            date = LocalDate.ofEpochDay(dateEpochDay),
            note = note,
            imageUri = imageUri,
            authorName = authorName
        )
    }
}