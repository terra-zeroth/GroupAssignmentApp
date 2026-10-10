package com.example.groupprojectapp.documentation.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

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
        val entry = DocumentationEntry(
            taskId = taskId,
            dateEpochDay = date.toEpochDay(),
            note = note,
            imageUri = imageUri,
            authorName = authorName
        )
        documentationDao.insertEntry(entry)
    }
}