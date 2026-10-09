package com.example.groupprojectapp.tasks.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate

/**
 * Single source of truth for task/member data. ViewModels never talk to the
 * DAOs directly — that keeps Room out of the UI layer, per R2.
 * Flows (allTasks, allMembers) are live feeds; the suspend functions are writes.
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

    /**
     * Keeps TODO/IN_PROGRESS in sync with today's date, so the Timeline's
     * colour coding is correct without anyone manually flipping a status.
     * DONE is the one manual override (set via the checkbox in the task
     * list) — this never touches a task that's already DONE, regardless
     * of its dates.
     *
     * Runs once whenever TaskContainer is created: on app start, and again
     * on rotation (since the container is recreated then too). Not a
     * continuously running background job — not required for this app.
     */
    suspend fun syncAutoStatuses(today: LocalDate = LocalDate.now()) {
        val todayEpoch = today.toEpochDay()
        val tasks = allTasks.first().map { it.task }
        tasks.forEach { task ->
            if (task.status == TaskStatus.DONE) return@forEach
            val inWindow = todayEpoch in task.startDateEpochDay..task.dueDateEpochDay
            val desiredStatus = if (inWindow) TaskStatus.IN_PROGRESS else TaskStatus.TODO
            if (task.status != desiredStatus) {
                taskDao.updateTask(task.copy(status = desiredStatus))
            }
        }
    }
}
