package com.example.groupprojectapp.tasks

import android.content.Context
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

    init {
        // Seed the assignee list from the real login accounts (UserData.kt)
        // the first time this ever runs, without editing UserData.kt.
        CoroutineScope(Dispatchers.IO).launch {
            taskRepository.seedMembersIfEmpty(userDatabase.keys.toList())
        }
    }
}
