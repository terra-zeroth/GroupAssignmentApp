package com.example.groupprojectapp.tasks

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

enum class TaskSortOrder { DUE_DATE, PRIORITY, ASSIGNEE }

private val Context.taskDataStore by preferencesDataStore(name = "task_prefs")

/**
 * R3 needs one DataStore preference that visibly changes app behaviour —
 * this is it. Changing the sort order on the Tasks screen re-sorts the list
 * immediately, because TaskListViewModel combines this Flow with the task
 * Flow coming from Room.
 */
class TaskPreferencesRepository(private val context: Context) {

    private object Keys {
        val SORT_ORDER = stringPreferencesKey("task_sort_order")
    }

    val sortOrder: Flow<TaskSortOrder> = context.taskDataStore.data.map { prefs ->
        val stored = prefs[Keys.SORT_ORDER] ?: TaskSortOrder.DUE_DATE.name
        runCatching { TaskSortOrder.valueOf(stored) }.getOrDefault(TaskSortOrder.DUE_DATE)
    }

    suspend fun setSortOrder(order: TaskSortOrder) {
        context.taskDataStore.edit { prefs -> prefs[Keys.SORT_ORDER] = order.name }
    }
}
