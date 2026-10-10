package com.example.groupprojectapp.documentation.ui.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import com.example.groupprojectapp.tasks.data.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddDocumentationViewModel(
    private val documentationRepository: DocumentationRepository,
    taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDocumentationUiState())
    val uiState: StateFlow<AddDocumentationUiState> = _uiState.asStateFlow()

    // Expose available tasks for the picker dropdown/list
    val taskOptions: StateFlow<List<TaskOption>> = taskRepository.allTasks
        .map { tasks ->
            tasks.map { TaskOption(id = it.task.id, title = it.task.title) }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun onTaskSelected(taskId: Long) {
        _uiState.update {
            it.copy(selectedTaskId = taskId, taskError = null)
        }
    }

    fun onNoteChanged(newNote: String) {
        _uiState.update {
            it.copy(note = newNote, noteError = null)
        }
    }

    fun saveEntry(onSaved: () -> Unit) {
        val currentState = _uiState.value

        val taskErr = if (currentState.selectedTaskId == null) {
            "Please select a task"
        } else {
            null
        }

        val noteErr = if (currentState.note.isBlank()) {
            "Note cannot be blank"
        } else {
            null
        }

        if (taskErr != null || noteErr != null) {
            _uiState.update {
                it.copy(taskError = taskErr, noteError = noteErr)
            }
            return
        }

        viewModelScope.launch {
            documentationRepository.addEntry(
                taskId = currentState.selectedTaskId!!,
                date = currentState.date,
                note = currentState.note.trim(),
                imageUri = currentState.imageUri
            )
            resetForm()
            onSaved()
        }
    }

    fun resetForm() {
        _uiState.value = AddDocumentationUiState()
    }
}