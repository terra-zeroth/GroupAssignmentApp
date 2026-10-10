package com.example.groupprojectapp.documentation.ui.list

// isLoading is for a spinner, starts true to show spinner until database starts
// filterTaskTittle is null when showing all entries
//  which is set when screen is opened from one task on the timeline

data class DocumentationUiState(
    val isLoading: Boolean = true,
    val entries: List<DocumentationEntryItem> = emptyList(),
    val filterTaskTitle: String? = null
)