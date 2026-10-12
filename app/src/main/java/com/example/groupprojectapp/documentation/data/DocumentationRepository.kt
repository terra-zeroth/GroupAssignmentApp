package com.example.groupprojectapp.documentation.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * MODEL (data layer):
 * the only way the ViewModels reach documentation data,
 * so they never call the DAO directly.
 *
 * It also owns the storage format: ViewModels work with LocalDate and this
 * class converts it to the epoch day (a Long) that Room stores.
 */

class DocumentationRepository(private val documentationDao: DocumentationDao) {

    val allEntriesWithTask: Flow<List<DocumentationEntryWithTask>> =
        documentationDao.getAllEntriesWithTaskTitle()

    fun getEntriesForTask(taskId: Long): Flow<List<DocumentationEntryWithTask>> =
        documentationDao.getEntriesForTask(taskId)

    suspend fun addEntry(
        taskId: Long,
        date: LocalDate,
        note: String,
        imageUri: String? = null,
        authorName: String
    ) {
        // id is left out (defaults to 0) so Room generates a new one
        val entry = DocumentationEntry(
            taskId = taskId,
            dateEpochDay = date.toEpochDay(),
            note = note,
            imageUri = imageUri,
            authorName = authorName
        )
        documentationDao.insertEntry(entry)
    }

    suspend fun getEntry(id: Long): DocumentationEntry? {
        return documentationDao.getEntryById(id)
    }

    suspend fun updateEntry(
        id: Long,
        taskId: Long,
        date: LocalDate,
        note: String,
        imageUri: String?,
        authorName: String
    ) {
        // Keeps the existing id, which is how Room knows which row to replace
        val entry = DocumentationEntry(
            id = id,
            taskId = taskId,
            dateEpochDay = date.toEpochDay(),
            note = note,
            imageUri = imageUri,
            authorName = authorName
        )
        documentationDao.updateEntry(entry)
    }
}