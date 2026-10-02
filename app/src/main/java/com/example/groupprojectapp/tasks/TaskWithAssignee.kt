package com.example.groupprojectapp.tasks

import androidx.room.Embedded
import androidx.room.Relation

data class TaskWithAssignee(
    @Embedded val task: Task,
    @Relation(parentColumn = "assigneeId", entityColumn = "id")
    val assignee: Member?
)
