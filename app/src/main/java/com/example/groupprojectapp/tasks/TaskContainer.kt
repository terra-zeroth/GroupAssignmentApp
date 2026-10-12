package com.example.groupprojectapp.tasks

import android.content.Context
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import com.example.groupprojectapp.tasks.data.TaskPreferencesRepository
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TasksDatabase
import com.example.groupprojectapp.userDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Manual, no-framework dependency container scoped to the Tasks feature.
 * Deliberately self-contained (its own Room database, its own DataStore)
 * so it doesn't require agreeing on a shared database/DI setup with the
 * rest of the app before this can ship — that consolidation can happen
 * later once everyone's parts exist.
 */
class TaskContainer(context: Context) {
    private val database = TasksDatabase.getInstance(context)

    val taskRepository = TaskRepository(database.taskDao(), database.memberDao())
    val preferencesRepository = TaskPreferencesRepository(context)

    val documentationRepository = DocumentationRepository(database.documentationDao())

    init {
        CoroutineScope(Dispatchers.IO).launch {
            // Seed the assignee list from the real login accounts (UserData.kt)
            // the first time this ever runs, without editing UserData.kt.
            taskRepository.seedMembersIfEmpty(userDatabase.keys.toList())
            // Auto-promote TODO -> IN_PROGRESS for any task whose date
            // window has started (and back again if dates get edited), so
            // the Timeline's colour coding is correct with no manual step.
            // DONE always overrides this.
            taskRepository.syncAutoStatuses()
        }
    }
}
