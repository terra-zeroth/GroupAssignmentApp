package com.example.groupprojectapp.documentation.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentationDao {

    @Query("""
        SELECT documentation_entries.*, tasks.title AS taskTitle 
        FROM documentation_entries 
        INNER JOIN tasks ON documentation_entries.taskId = tasks.id
        ORDER BY documentation_entries.dateEpochDay DESC
    """)

    fun getAllEntriesWithTaskTitle(): Flow<List<DocumentationEntryWithTask>>

    @Query("""
        SELECT documentation_entries.*, tasks.title AS taskTitle 
        FROM documentation_entries 
        INNER JOIN tasks ON documentation_entries.taskId = tasks.id
        WHERE documentation_entries.taskId = :taskId
        ORDER BY documentation_entries.dateEpochDay DESC
    """)

    fun getEntriesForTask(taskId: Long): Flow<List<DocumentationEntryWithTask>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)

    suspend fun insertEntry(entry: DocumentationEntry)

    // Be able to edit existing documentations:
    @Query("SELECT * FROM documentation_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): DocumentationEntry?

    @Update
    suspend fun updateEntry(entry: DocumentationEntry)
}