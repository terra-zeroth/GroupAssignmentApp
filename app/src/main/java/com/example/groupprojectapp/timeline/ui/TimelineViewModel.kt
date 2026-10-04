package com.example.groupprojectapp.timeline.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.tasks.data.TaskWithAssignee
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.time.LocalDate

class TimelineViewModel(
    private val repository: TaskRepository
) : ViewModel(){

    val uiState: StateFlow<TimelineUiState> = repository.allTasks
        .map { tasks -> calculateTimelineState(tasks) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = TimelineUiState() // Initial state with isLoading = true
        )

    private fun calculateTimelineState(tasks: List<TaskWithAssignee>): TimelineUiState {
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
                isLoading = false
            )

        } else {
            // Calculate date bounds

            // Earliest start date and latest due date (bounded by today)
            val minTaskStart = tasks.minOf { it.task.startDateEpochDay }
            val maxTaskDue = tasks.maxOf { it.task.dueDateEpochDay }

            val rangeStartEpoch = minOf(minTaskStart, todayEpochDay)
            val rangeEndEpoch = maxOf(maxTaskDue, todayEpochDay)

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
                    title = task.title,
                    status = task.status,
                    assigneeName = assigneeName,
                    startOffsetDays = startOffset,
                    lengthDays = lengthDays,
                    isOverdue = isOverdue

                )

            }

            // sort the items by start date so the chart reads top to bottom in time order
            val sortedItems = timelineItems.sortedBy { it.startOffsetDays }
            val todayOffsetDay = (todayEpochDay - rangeStartEpoch).toInt() // calculate current day marker offset

            state = TimelineUiState(
                items = sortedItems,
                firstDate = firstDate,
                totalNumDays = totalNumDays,
                todayOffset = todayOffsetDay,
                isLoading = false
            )
        }

        return state
    }

}