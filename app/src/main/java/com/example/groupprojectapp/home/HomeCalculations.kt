package com.example.groupprojectapp.home

import com.example.groupprojectapp.tasks.data.TaskPriority
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.tasks.data.TaskWithAssignee
import java.time.LocalDate

/**
 * Pure calculations for the Home screen's "Completion" and "Current To-Do"
 * boxes. No Compose, no Room — just numbers derived from the already-loaded
 * task list, so this is easy to reuse or test on its own (R2).
 */
object HomeCalculations {

    /**
     * Percentage (0-100) of the logged-in user's OWN tasks that are DONE
     * (not the whole team's). 0 when they have no tasks yet.
     */
    fun completionPercent(tasks: List<TaskWithAssignee>, userName: String): Int {
        val myTasks = tasks.filter { it.assignee?.name == userName }
        if (myTasks.isEmpty()) return 0
        val done = myTasks.count { it.task.status == TaskStatus.DONE }
        return (done * 100) / myTasks.size
    }

    /**
     * The logged-in user's next [limit] open tasks, soonest due date first.
     * Won't-priority tasks are excluded since they're deprioritised by
     * definition and don't count as a "current" to-do.
     */
    fun currentTasks(
        tasks: List<TaskWithAssignee>,
        userName: String,
        limit: Int = 3
    ): List<TaskWithAssignee> =
        tasks
            .filter {
                it.assignee?.name == userName &&
                    it.task.status != TaskStatus.DONE &&
                    it.task.priority != TaskPriority.WONT
            }
            .sortedBy { it.task.dueDateEpochDay }
            .take(limit)

    /** Matches the Timeline's own overdue rule: not done, and due date already passed. */
    fun isOverdue(task: TaskWithAssignee, today: LocalDate = LocalDate.now()): Boolean =
        task.task.status != TaskStatus.DONE && task.task.dueDateEpochDay < today.toEpochDay()
}
