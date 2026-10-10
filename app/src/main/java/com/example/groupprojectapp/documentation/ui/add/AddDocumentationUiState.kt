package com.example.groupprojectapp.documentation.ui.add

import java.time.LocalDate

data class AddDocumentationUiState(
    val selectedTaskId: Long? = null,
    val date: LocalDate = LocalDate.now(),
    val note: String = "",
    val taskError: String? = null,
    val noteError: String? = null,
    val imageUri: String? = null
)