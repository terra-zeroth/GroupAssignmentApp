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
import java.time.LocalDate

/**
 * VIEWMODEL (MVVM):
 * holds the add/edit form's state and does the form's
 * logic: validation, loading an entry to edit, and saving (R2).
 *
 * MainActivity owns this ViewModel, so it outlives the form screen.
 * That's why the form is reset after saving and on Back; otherwise the
 * next time the form opens it would still show the old values.
 */

class AddDocumentationViewModel(
    private val documentationRepository: DocumentationRepository,
    taskRepository: TaskRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddDocumentationUiState())
    val uiState: StateFlow<AddDocumentationUiState> = _uiState.asStateFlow()

    // The task picker's choices; updates by itself if tasks change
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

    // allow the user to change the date of documentation
    fun onDateChange(newDate: LocalDate) {
        _uiState.update { it.copy(date = newDate) }
    }

    // called when an image is successfully picked from the gallery
    fun onImagePicked(uriString: String) {
        _uiState.update { it.copy(imageUri = uriString) }
    }

    // called when the user removes the attached image
    fun onImageRemoved() {
        _uiState.update { it.copy(imageUri = null) }
    }

    // One save for both modes. Validates first, then adds a new entry (author =
    // whoever is logged in now) or updates the edited one (original author kept).
    // The username is passed in at save time, not in the constructor, so it's
    // never a stale name from an earlier login.
    fun saveEntry(currentUserName: String, onSaved: () -> Unit) {
        val state = _uiState.value
        val taskId = state.selectedTaskId

        val taskErr = if (taskId == null) "Please select a task" else null
        val noteErr = if (state.note.isBlank()) "Note cannot be blank" else null

        if (taskId == null || noteErr != null) {
            _uiState.update { it.copy(taskError = taskErr, noteError = noteErr) }
            return
        }

        viewModelScope.launch {
            val entryId = state.editingEntryId
            if (entryId == null) {
                // add mode: logged-in user is the author
                documentationRepository.addEntry(
                    taskId = taskId,
                    date = state.date,
                    note = state.note.trim(),
                    imageUri = state.imageUri,
                    authorName = currentUserName
                )
            } else {
                // edit mode: keep the original author
                documentationRepository.updateEntry(
                    id = entryId,
                    taskId = taskId,
                    date = state.date,
                    note = state.note.trim(),
                    imageUri = state.imageUri,
                    authorName = state.authorName
                )
            }
            resetForm()
            onSaved()
        }
    }

    // Back to an empty form (also clears editingEntryId and authorName)
    fun resetForm() {
        _uiState.value = AddDocumentationUiState()
    }

    // Fills the form with a saved entry for editing. Skips if that entry is
    // already loaded, so rotating mid-edit doesn't overwrite the user's changes.
    fun loadEntry(entryId: Long) {
        // if already editing this entry, don't reload (protects against rotation)
        if (_uiState.value.editingEntryId == entryId) return

        viewModelScope.launch {
            documentationRepository.getEntry(entryId)?.let { entry ->
                _uiState.update { state ->
                    state.copy(
                        selectedTaskId = entry.taskId,
                        date = LocalDate.ofEpochDay(entry.dateEpochDay),
                        note = entry.note,
                        imageUri = entry.imageUri,
                        authorName = entry.authorName,
                        editingEntryId = entry.id
                    )
                }
            }
        }
    }
}