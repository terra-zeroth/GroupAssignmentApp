package com.example.groupprojectapp.documentation.data

import androidx.room.ColumnInfo

data class DocumentationEntryWithTask(
    val id: Long,
    val taskId: Long,
    val dateEpochDay: Long,
    val note: String,
    val imageUri: String?,
    val authorName: String,
    @ColumnInfo(name = "taskTitle") val taskTitle: String
)