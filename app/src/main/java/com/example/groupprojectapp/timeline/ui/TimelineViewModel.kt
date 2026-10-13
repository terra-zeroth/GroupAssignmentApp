package com.example.groupprojectapp.timeline.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.tasks.data.TaskWithAssignee
import com.example.groupprojectapp.timeline.ui.components.TimelineStatusCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.time.LocalDate

/**
 * VIEWMODEL (MVVM):
 * turns the task list into timeline state.
 *
 * What it does: combines repository.allTasks (a live Flow, so edits in the
 * Tasks screen show up here automatically) with the status filter, converts
 * each task into a [TimelineItem] and exposes the result as [uiState].
 *
 * Business logic kept here (R2), not in composables:
 * - the chart's date range (earliest start / latest due, always including
 *   today, plus one day of padding at each end), worked out from ALL tasks
 *   so the dates don't jump when the filter changes
 * - each bar's start offset and length in days (length is at least 1)
 * - overdue = due before today and not DONE (same rule as TaskListViewModel)
 * - "Unassigned" when a task has no assignee
 * - row order: start, then length, then title
 * - the status filter: which categories are shown (overdue checked first)
 *
 * What it does NOT do: no drawing and no dp/sp sizes. It works only in days.
 *
 * Owner: requested in TimelineFeature with viewModel(); MainActivity is the
 * owner (no NavHost), so the instance (and the filter) survives rotation and
 * leaving/returning to the Timeline.
 */

class TimelineViewModel(
    private val repository: TaskRepository
) : ViewModel(){

    // Which status categories are ticked. Starts with all four (show everything).
    private val selectedCategories =
        MutableStateFlow(TimelineStatusCategory.entries.toSet())

    val uiState: StateFlow<TimelineUiState> =
        combine(repository.allTasks, selectedCategories) { tasks, selected ->
            calculateTimelineState(tasks, selected)
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TimelineUiState() // Initial state with isLoading = true
            )

    /**
     * Ticks or unticks a category: removes it if it's selected, adds it if not.
     * update makes the read-and-write one safe step.
     */
    fun toggleFilter(category: TimelineStatusCategory) {
        selectedCategories.update { current ->
            if (category in current) current - category else current + category
        }
    }

    /**
     * Which category an item belongs to. Overdue is checked first,
     * the same order as the bar colours and the status text.
     */
    private fun categoryOf(item: TimelineItem): TimelineStatusCategory = when {
        item.isOverdue -> TimelineStatusCategory.OVERDUE
        else -> when (item.status) {
            TaskStatus.TODO -> TimelineStatusCategory.TODO
            TaskStatus.IN_PROGRESS -> TimelineStatusCategory.IN_PROGRESS
            TaskStatus.DONE -> TimelineStatusCategory.DONE
        }
    }

    /**
     * Builds a complete [TimelineUiState] from the current task list and filter.
     * An empty list returns an empty state early, because min/max would
     * crash on an empty list.
     */
    private fun calculateTimelineState(
        tasks: List<TaskWithAssignee>,
        selected: Set<TimelineStatusCategory>
    ): TimelineUiState {
        val todayLocalDay = LocalDate.now()
        val todayEpochDay = todayLocalDay.toEpochDay()
        val state: TimelineUiState

        // in case for empty task list
        // this skips the min/max maths, which would crash on an empty list
        if (tasks.isEmpty()) {
            state = TimelineUiState(
                items = emptyList(),
                firstDate = todayLocalDay,
                totalNumDays = 1,
                todayOffset = 0,
                isLoading = false,
                selectedCategories = selected,
                totalTaskCount = 0
            )

        } else {
            // Calculate date bounds from ALL tasks (not just filtered ones),
            // so the chart's dates don't jump and auto-centring isn't thrown off

            // Earliest start date and latest due date (bounded by today)
            val minTaskStart = tasks.minOf { it.task.startDateEpochDay }
            val maxTaskDue = tasks.maxOf { it.task.dueDateEpochDay }

            // Calculate bounds with 1 day padding on each end
            val rangeStartEpoch = minOf(minTaskStart, todayEpochDay) - 1
            val rangeEndEpoch = maxOf(maxTaskDue, todayEpochDay) + 1

            val totalNumDays = (rangeEndEpoch - rangeStartEpoch + 1).toInt()
            val firstDate = LocalDate.ofEpochDay(rangeStartEpoch)

            // Map tasks to TimelineItems
            val timelineItems = tasks.map { item ->
                val task = item.task
                val startOffset = (task.startDateEpochDay - rangeStartEpoch).toInt()

                // ensure that length is at least one day:
                val calculatedLength = (task.dueDateEpochDay - task.startDateEpochDay + 1).toInt()
                val lengthDays = maxOf(1, calculatedLength)

                // due date before today and status isn't done from TaskListViewModel
                val isOverdue = task.dueDateEpochDay < todayEpochDay && task.status != TaskStatus.DONE

                // null safety practice since it checks assignee name or "Unassigned" if the assignee is null using Elvis operator
                val assigneeName = item.assignee?.name ?: "Unassigned"

                TimelineItem(
                    taskId = task.id,
                    startDate = LocalDate.ofEpochDay(task.startDateEpochDay),
                    dueDate = LocalDate.ofEpochDay(task.dueDateEpochDay),
                    title = task.title,
                    status = task.status,
                    assigneeName = assigneeName,
                    startOffsetDays = startOffset,
                    lengthDays = lengthDays,
                    isOverdue = isOverdue
                )
            }

            // sort the items by start offset then by task length (due date) so the chart reads top to bottom in time order
            val sortedItems = timelineItems.sortedWith(
                compareBy({ it.startOffsetDays }, { it.lengthDays }, { it.title })
            )

            // keep only the categories that are ticked
            val visibleItems = sortedItems.filter { categoryOf(it) in selected }

            val todayOffsetDay = (todayEpochDay - rangeStartEpoch).toInt() // calculate current day marker offset

            state = TimelineUiState(
                items = visibleItems,
                firstDate = firstDate,
                totalNumDays = totalNumDays,
                todayOffset = todayOffsetDay,
                isLoading = false,
                selectedCategories = selected,
                totalTaskCount = tasks.size
            )
        }

        return state
    }

}