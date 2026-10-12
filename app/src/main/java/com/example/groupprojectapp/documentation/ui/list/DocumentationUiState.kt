package com.example.groupprojectapp.documentation.ui.list

/**
 * UI STATE (MVVM):
 * the complete state of the Documentation list screen,
 * exposed by DocumentationViewModel as a StateFlow and drawn by
 * DocumentationContent.
 *
 * isLoading starts true so a spinner shows until the database answers.
 * filterTaskTitle is null when showing all entries; it is set when the
 * screen is opened from one task on the timeline (A5), and is also shown
 * as the top bar title.
 */
data class DocumentationUiState(
    val isLoading: Boolean = true,
    val entries: List<DocumentationEntryItem> = emptyList(),
    val filterTaskTitle: String? = null
)