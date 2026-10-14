package com.example.groupprojectapp.documentation.data

import androidx.room.ColumnInfo
import com.example.groupprojectapp.tasks.data.TaskStatus

/**
 * MODEL (data layer):
 * what the DAO's JOIN queries return. One entry plus
 * the title, status and due date of the task it belongs to
 * (from "tasks.title AS taskTitle", "tasks.status AS taskStatus" and
 * "tasks.dueDateEpochDay AS taskDueEpochDay").
 *
 * Not a table, so no @Entity: Room fills it by matching column names
 * to these field names, so the names must match the AS aliases exactly.
 * DocumentationViewModel turns it into a DocumentationEntryItem for the screen.
 */

data class DocumentationEntryWithTask(
    val id: Long,
    val taskId: Long,
    val dateEpochDay: Long,
    val note: String,
    val imageUri: String?,
    val authorName: String,
    @ColumnInfo(name = "taskTitle") val taskTitle: String,
    @ColumnInfo(name = "taskStatus") val taskStatus: TaskStatus,
    @ColumnInfo(name = "taskDueEpochDay") val taskDueEpochDay: Long
)