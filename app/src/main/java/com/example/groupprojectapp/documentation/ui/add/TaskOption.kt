package com.example.groupprojectapp.documentation.ui.add

/**
 * UI MODEL:
 * one choice in the form's task picker. Only the id (saved as
 * the entry's foreign key) and the title (what the user reads), so the
 * form doesn't need the whole Task entity.
 */

data class TaskOption(
    val id: Long,
    val title: String
)
