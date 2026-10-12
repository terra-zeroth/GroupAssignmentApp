package com.example.groupprojectapp.documentation.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.groupprojectapp.tasks.data.Task

/**
 * MODEL (data layer):
 * one row of the documentation_entries table (A3)
 *
 * Each entry belongs to exactly one task (many entries -> one task)
 * taskId is a foreign key to tasks.id, so an entry can't point at a task
 * that doesn't exist, and deleting a task also deletes its entries (CASCADE)
 * The index on taskId makes "entries for this task" lookups fast; Room
 * warns if a foreign key column has no index
 *
 * Stored in database-friendly types (date as an epoch day); the repository
 * converts to LocalDate for the ViewModels
 */
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
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // 0 = "new", Room assigns the real id
    val taskId: Long, // which task this entry belongs to
    val dateEpochDay: Long, // days since 1 Jan 1970, same style as task dates
    val note: String, // validated as not blank in the ViewModel
    val imageUri: String? = null, // A4: picked screenshot's address; null = no image
    val authorName: String // Stores who submitted it
)