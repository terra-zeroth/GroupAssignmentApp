package com.example.groupprojectapp.documentation.data

import androidx.room.ColumnInfo

/**
 * MODEL (data layer):
 * what the DAO's JOIN queries return. One entry plus
 * the title of the task it belongs to (from "tasks.title AS taskTitle").
 *
 * Not a table, so no @Entity: Room fills it by matching column names
 * to these field names. DocumentationViewModel turns it into a
 * DocumentationEntryItem for the screen.
 */

data class DocumentationEntryWithTask(
    val id: Long,
    val taskId: Long,
    val dateEpochDay: Long,
    val note: String,
    val imageUri: String?,
    val authorName: String,
    @ColumnInfo(name = "taskTitle") val taskTitle: String
)