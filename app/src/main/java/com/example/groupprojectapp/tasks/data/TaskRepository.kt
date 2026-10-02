package com.example.groupprojectapp.tasks.data

import kotlinx.coroutines.flow.Flow

/**
 * Single source of truth for task/member data. ViewModels never talk to the
 * DAOs directly — that keeps Room out of the UI layer, per R2.
 */
class TaskRepository(
    private val taskDao: TaskDao,
    private val memberDao: MemberDao
) {
    val allTasks: Flow<List<TaskWithAssignee>> = taskDao.getAllTasksWithAssignee()
    val allMembers: Flow<List<Member>> = memberDao.getAllMembers()

    fun taskById(id: Long): Flow<TaskWithAssignee?> = taskDao.getTaskById(id)

    suspend fun saveTask(task: Task): Long = taskDao.upsertTask(task)
    suspend fun updateTask(task: Task) = taskDao.updateTask(task)
    suspend fun deleteTask(task: Task) = taskDao.deleteTask(task)

    /**
     * One-time seed so "assignee" always lines up with the real login
     * accounts in UserData.kt, without touching that file. Does nothing
     * once members already exist (e.g. on every app start after the first).
     */
    suspend fun seedMembersIfEmpty(names: List<String>) {
        if (memberDao.getAllMembersOnce().isEmpty()) {
            names.forEach { memberDao.upsertMember(Member(name = it)) }
        }
    }
}