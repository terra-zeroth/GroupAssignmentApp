package com.example.groupprojectapp.documentation.ui.add

import java.time.LocalDate

/**
 * UI STATE (MVVM):
 * everything the add/edit form shows, held in
 * AddDocumentationViewModel so the values survive rotation (R1).
 *
 * selectedTaskId is null until the user picks a task (validated on save)
 * taskError / noteError hold a message to show under that field or null
 * imageUri is the attached screenshot's address or null
 * authorName is only filled when editing, to keep the original author
 * editingEntryId: null = adding a new entry; an id = editing that entry
 */

data class AddDocumentationUiState(
    val selectedTaskId: Long? = null,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val taskError: String? = null,
    val noteError: String? = null,
    val imageUri: String? = null,
    val authorName: String = "",
    val editingEntryId: Long? = null,
)