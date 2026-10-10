package com.example.groupprojectapp.documentation.ui.list

import java.time.LocalDate

// UI model for a single Compose UI card

data class DocumentationEntryItem(
    val id: Long,
    val taskTitle: String,
    val date: LocalDate,
    val note: String,
    val imageUri: String? = null
)
