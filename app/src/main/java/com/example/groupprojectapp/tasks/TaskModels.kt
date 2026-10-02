package com.example.groupprojectapp.tasks

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskStatus { TODO, IN_PROGRESS, DONE }

enum class TaskPriority { LOW, MEDIUM, HIGH }

/**
 * One row per real login account (seeded from UserData.kt's userDatabase,
 * not duplicated by hand) so "assignee" in the to-do list always matches
 * who can actually log in.
 */
@Entity(tableName = "members")
data class Member(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

/**
 * Related to [Member] via [assigneeId] — that relationship is job
 * allocation (R3 needs two *related* entities, not two tables that happen
 * to sit next to each other). [dependsOnTaskId] is a simple single-
 * prerequisite link Aliyah's timeline can use for ordering.
 */
@Entity(
    tableName = "tasks",
    foreignKeys = [
        ForeignKey(
            entity = Member::class,
            parentColumns = ["id"],
            childColumns = ["assigneeId"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = Task::class,
            parentColumns = ["id"],
            childColumns = ["dependsOnTaskId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("assigneeId"), Index("dependsOnTaskId")]
)
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String = "",
    val dueDateEpochDay: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val assigneeId: Long? = null,
    val dependsOnTaskId: Long? = null
)
