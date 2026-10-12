package com.example.groupprojectapp.documentation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * MODEL (data layer):
 * the queries for the documentation_entries table
 * Room writes the implementation at build time and checks the SQL then too,
 * so a wrong table or column name is a build error, not a crash
 *
 * The list queries JOIN the tasks table to add each entry's task title,
 * so the task <-> entry relationship is used, not just stored (R3)
 * They return Flow, so the list updates by itself after an add or edit
 */

@Dao
interface DocumentationDao {

    @Query("""
        SELECT documentation_entries.*, tasks.title AS taskTitle 
        FROM documentation_entries 
        INNER JOIN tasks ON documentation_entries.taskId = tasks.id
        ORDER BY documentation_entries.dateEpochDay DESC
    """)
    // All entries, newest date first, each with its task's title (list screen)
    fun getAllEntriesWithTaskTitle(): Flow<List<DocumentationEntryWithTask>>

    @Query("""
        SELECT documentation_entries.*, tasks.title AS taskTitle 
        FROM documentation_entries 
        INNER JOIN tasks ON documentation_entries.taskId = tasks.id
        WHERE documentation_entries.taskId = :taskId
        ORDER BY documentation_entries.dateEpochDay DESC
    """)
    // Only one task's entries, for opening documentation from the timeline (A5)
    fun getEntriesForTask(taskId: Long): Flow<List<DocumentationEntryWithTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    // Adds a new entry; Room generates its id
    suspend fun insertEntry(entry: DocumentationEntry)

    // Be able to edit existing documentations:
    @Query("SELECT * FROM documentation_entries WHERE id = :id")
    // Loads one entry into the form for editing; null if it no longer exists
    suspend fun getEntryById(id: Long): DocumentationEntry?

    @Update
    // Replaces an existing entry; Room finds the row by its id
    suspend fun updateEntry(entry: DocumentationEntry)
}