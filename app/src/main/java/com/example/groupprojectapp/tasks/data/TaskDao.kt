package com.example.groupprojectapp.tasks.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * MODEL (data layer): the database queries for tasks. Room writes the
 * implementation for us from these annotations.
 *
 * The two reads return a Flow, so they send a fresh list whenever the tasks
 * table changes. That is why the UI updates without a manual refresh.
 * The writes are `suspend` functions so they run off the main thread.
 * Reads return [TaskWithAssignee] so each task arrives with its Member attached.
 */

@Dao
interface TaskDao {

    @Transaction
    @Query("SELECT * FROM tasks ORDER BY dueDateEpochDay ASC")
    fun getAllTasksWithAssignee(): Flow<List<TaskWithAssignee>>

    @Transaction
    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskById(taskId: Long): Flow<TaskWithAssignee?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: Task): Long

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)
}