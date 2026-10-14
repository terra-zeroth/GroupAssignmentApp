package com.example.groupprojectapp.documentation.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.documentation.data.DocumentationEntryWithTask
import com.example.groupprojectapp.documentation.data.DocumentationRepository
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * VIEWMODEL (MVVM):
 * turns documentation entries from the database into
 * [DocumentationUiState] for the list screen.
 *
 * taskId decides what is shown: null = all entries (opened from Home);
 * an id = only that task's entries (opened from the timeline, A5).
 * That first filter happens in the DAO query, never in the composable (R2).
 *
 * Status and author filters are held in [filters] and applied here, after
 * each row becomes a DocumentationEntryItem, because "Overdue" only exists
 * once isOverdue is worked out. The composable only draws the result (R2).
 *
 * Flows combined:
 * - entries: the rows to show (each already has its task's status and due date)
 * - tasks: the filtered task's title for the top bar
 * - members: names for the author chips
 * - filters: the current filter choices
 *
 * Overdue uses the same rule as TaskListViewModel and TimelineViewModel:
 * due before today and not DONE.
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

    // Current filter choices. Starts with everything off.
    private val filters = MutableStateFlow(DocumentationFilters())

    val uiState: StateFlow<DocumentationUiState> =
        combine(
            entriesFlow,
            taskRepository.allTasks,
            taskRepository.allMembers,
            filters
        ) { entries, tasks, members, currentFilters ->

            // Look up the task title by ID (null when not filtering)
            val title = taskId?.let { id ->
                tasks.find { it.task.id == id }?.task?.title
            }

            // Worked out once per update, not once per entry
            val todayEpochDay = LocalDate.now().toEpochDay()

            // Convert first, so each item already knows isOverdue
            val allItems = entries.map { it.toUiItem(todayEpochDay) }

            // Keep an item only if it passes both checks (ALL / null = keep everything)
            val filteredItems = allItems.filter { item ->
                matchesStatus(item, currentFilters.status) &&
                        (currentFilters.selectedAuthor == null ||
                                item.authorName == currentFilters.selectedAuthor)
            }

            // Author options: members plus anyone who has written an entry,
            // so authors who aren't task members can still be filtered
            val authorOptions = (members.map { it.name } + entries.map { it.authorName })
                .distinct()
                .sorted()

            DocumentationUiState(
                isLoading = false,
                entries = filteredItems,
                filterTaskTitle = title,
                filters = currentFilters,
                authorOptions = authorOptions
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = DocumentationUiState()
            )

    // EVENTS (from the top-bar menu and author chips)

    /** Status picked from the top-bar menu. */
    fun onStatusFilterSelected(status: StatusFilter) {
        filters.update { it.copy(status = status) }
    }

    /** Author chip tapped; null = All. */
    fun onAuthorFilterSelected(author: String?) {
        filters.update { it.copy(selectedAuthor = author) }
    }

    /** Turns every filter off. */
    fun clearFilters() {
        filters.update { DocumentationFilters() }
    }

    /**
     * Whether an item belongs in the chosen status.
     * Overdue is its own group, so To do / In progress leave out overdue tasks
     * (same grouping as the Timeline legend).
     */
    private fun matchesStatus(item: DocumentationEntryItem, status: StatusFilter): Boolean =
        when (status) {
            StatusFilter.ALL -> true
            StatusFilter.OVERDUE -> item.isOverdue
            StatusFilter.TODO -> !item.isOverdue && item.status == TaskStatus.TODO
            StatusFilter.IN_PROGRESS -> !item.isOverdue && item.status == TaskStatus.IN_PROGRESS
            StatusFilter.DONE -> item.status == TaskStatus.DONE
        }

    // Converts a database row into what one card displays:
    // epoch day -> LocalDate; overdue worked out from the task's due date and status;
    // everything else is copied across
    private fun DocumentationEntryWithTask.toUiItem(todayEpochDay: Long): DocumentationEntryItem {
        // due date before today and status isn't done, same rule as TaskListViewModel
        val isOverdue = taskDueEpochDay < todayEpochDay && taskStatus != TaskStatus.DONE

        return DocumentationEntryItem(
            id = id,
            taskTitle = taskTitle,
            date = LocalDate.ofEpochDay(dateEpochDay),
            note = note,
            imageUri = imageUri,
            authorName = authorName,
            status = taskStatus,
            isOverdue = isOverdue
        )
    }
}