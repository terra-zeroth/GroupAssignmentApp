package com.example.groupprojectapp.documentation.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.groupprojectapp.tasks.data.Task

// represent row in Room database table

@Entity(
    tableName = "documentation_entries",
    foreignKeys = [
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["taskId"],
            onDelete = ForeignKey.CASCADE // Deleting a task automatically cleans up its docs
        )
    ],
    indices = [Index("taskId")]
)

data class DocumentationEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val taskId: Long,
    val dateEpochDay: Long,
    val note: String,
    val imageUri: String? = null
)