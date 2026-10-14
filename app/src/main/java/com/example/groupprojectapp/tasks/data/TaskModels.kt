package com.example.groupprojectapp.tasks.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class TaskStatus { TODO, IN_PROGRESS, DONE }

/**
 * MoSCoW priority (requirements-engineering style tagging), replacing the
 * old Low/Medium/High scale per the team plan (M6).
 *
 * Declared in THIS exact order — WONT first, MUST last — because Kotlin's
 * enum .ordinal follows declaration order, and TaskListViewModel sorts
 * "most urgent first" using sortedByDescending { priority.ordinal }. MUST
 * needs the highest ordinal (3) so it sorts to the top; WONT needs the
 * lowest (0) so it sorts to the bottom. Don't reorder these without also
 * checking that sort still makes sense.
 */
enum class TaskPriority { WONT, COULD, SHOULD, MUST }

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
    val startDateEpochDay: Long,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: TaskPriority = TaskPriority.SHOULD,
    val assigneeId: Long? = null,
    val dependsOnTaskId: Long? = null
)
