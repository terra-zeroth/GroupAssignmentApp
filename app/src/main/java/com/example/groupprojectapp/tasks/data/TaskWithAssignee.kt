package com.example.groupprojectapp.tasks.data

import androidx.room.Embedded
import androidx.room.Relation

/**
 * MODEL (data layer): a Task bundled with the Member it is assigned to
 * (null if unassigned). `@Embedded` pulls the task's columns in directly,
 * and `@Relation` tells Room to look up the Member whose `id` matches the
 * task's `assigneeId`. This lets the list show names without a second query.
 */

data class TaskWithAssignee(
    @Embedded val task: Task,
    @Relation(parentColumn = "assigneeId", entityColumn = "id")
    val assignee: Member?
)