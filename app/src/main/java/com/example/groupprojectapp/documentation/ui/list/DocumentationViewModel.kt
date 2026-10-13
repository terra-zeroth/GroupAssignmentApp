package com.example.groupprojectapp.documentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.documentation.data.DocumentationEntryWithTask
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import com.example.groupprojectapp.tasks.data.Task
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TaskStatus
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

/**
 * VIEWMODEL (MVVM):
 * turns documentation entries from the database into
 * [DocumentationUiState] for the list screen.
 *
 * taskId decides what is shown: null = all entries (opened from Home);
 * an id = only that task's entries (opened from the timeline, A5).
 * Filtering happens in the DAO query, never in the composable (R2).
 *
 * The entries are combined with the task list so the top bar can show the
 * filtered task's title even before it has any entries, and so each card
 * knows its task's status (for the Completed / Overdue colours). Because the
 * task list is a live Flow, cards update as soon as a task's status changes.
 *
 * Overdue uses the same rule as TaskListViewModel and TimelineViewModel:
 * due before today and not DONE (R2: worked out here, not in the composable).
 *
 * stateIn starts with DocumentationUiState() (isLoading = true) and keeps
 * the database flows alive for 5 seconds after the screen stops listening,
 * so a rotation doesn't restart the queries.
 */

class DocumentationViewModel(
    repository: DocumentationRepository,
    taskRepository: TaskRepository,
    private val taskId: Long? = null
) : ViewModel() {

    // Pick the query once: one task's entries, or all of them
    private val entriesFlow =
        if (taskId == null) repository.allEntriesWithTask
        else repository.getEntriesForTask(taskId)

    val uiState: StateFlow<DocumentationUiState> =
    // allow the viewModel to have both the entry flow and task flow
    // so the Top bar can show the task's title even without entries yet,
        // and each card can look up its task's status
        combine(entriesFlow, taskRepository.allTasks) { entries, tasks ->

            // Look up the task title by ID (null when not filtering)
            val title = taskId?.let { id ->
                tasks.find { it.task.id == id }?.task?.title
            }

            // Map of task id -> task, so each entry can find its task quickly
            val tasksById = tasks.associate { it.task.id to it.task }
            val todayEpochDay = LocalDate.now().toEpochDay()

            DocumentationUiState(
                isLoading = false,
                entries = entries.map { entry ->
                    entry.toUiItem(
                        task = tasksById[entry.taskId],
                        todayEpochDay = todayEpochDay
                    )
                },
                filterTaskTitle = title
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DocumentationUiState()
            )

    // Converts a database row into what one card displays:
    // epoch day -> LocalDate; status and overdue come from the entry's task;
    // everything else is copied across
    private fun DocumentationEntryWithTask.toUiItem(
        task: Task?,
        todayEpochDay: Long
    ): DocumentationEntryItem {
        // task should always exist (foreign key + CASCADE), but fall back safely if not
        val status = task?.status ?: TaskStatus.TODO

        // due date before today and status isn't done, same rule as TimelineViewModel
        val isOverdue = task != null &&
                task.dueDateEpochDay < todayEpochDay &&
                task.status != TaskStatus.DONE

        return DocumentationEntryItem(
            id = id,
            taskTitle = taskTitle,
            date = LocalDate.ofEpochDay(dateEpochDay),
            note = note,
            imageUri = imageUri,
            authorName = authorName,
            status = status,
            isOverdue = isOverdue
        )
    }
}