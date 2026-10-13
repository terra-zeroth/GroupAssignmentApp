package com.example.groupprojectapp.documentation.ui.list

import com.example.groupprojectapp.tasks.data.TaskStatus
import java.time.LocalDate

/**
 * UI MODEL (MVVM, produced by the ViewModel for the View):
 * everything one documentation card needs to display.
 *
 * Not the database row:
 * DocumentationEntry stores the task's id and the
 * date as an epoch day. DocumentationViewModel converts each
 * DocumentationEntryWithTask into this item (task title instead of id,
 * LocalDate instead of epoch day), so the composable never touches the
 * database or does conversions (R2).
 *
 * imageUri is null when the entry has no screenshot (A4).
 */

data class DocumentationEntryItem(
    val id: Long,
    val taskTitle: String,
    val date: LocalDate,
    val note: String,
    val imageUri: String? = null,
    val authorName: String,
    val status: TaskStatus = TaskStatus.TODO,
    val isOverdue: Boolean = false,
)
