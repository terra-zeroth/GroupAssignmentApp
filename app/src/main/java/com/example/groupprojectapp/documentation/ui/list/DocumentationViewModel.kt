package com.example.groupprojectapp.documentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.documentation.data.DocumentationEntryWithTask
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class DocumentationViewModel(
    private val repository: DocumentationRepository
) : ViewModel() {

    val uiState: StateFlow<DocumentationUiState> = repository.allEntriesWithTask
        .map { entries ->
            DocumentationUiState(
                isLoading = false,
                entries = entries.map { it.toUiItem() },
                filterTaskTitle = null
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
            imageUri = imageUri
        )
    }
}