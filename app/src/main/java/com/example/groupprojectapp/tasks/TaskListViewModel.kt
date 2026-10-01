package com.example.groupprojectapp.tasks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TaskListUiState(
    val tasks: List<TaskWithAssignee> = emptyList(),
    val members: List<Member> = emptyList(),
    val sortOrder: TaskSortOrder = TaskSortOrder.DUE_DATE,
    val selectedAssigneeId: Long? = null,
    val overdueCount: Int = 0,
    val isLoading: Boolean = true
)

/**
 * All the "logic" behind the to-do list — sorting, the overdue count, and
 * the job-allocation filter by assignee — happens here, not in the
 * composable. That's what R2 is asking for: composables just render
 * [TaskListUiState] and forward user events back up through the public
 * functions below.
 */
class TaskListViewModel(
    private val repository: TaskRepository,
    private val preferencesRepository: TaskPreferencesRepository
) : ViewModel() {

    private val selectedAssigneeId = MutableStateFlow<Long?>(null)

    val uiState: StateFlow<TaskListUiState> = combine(
        repository.allTasks,
        repository.allMembers,
        preferencesRepository.sortOrder,
        selectedAssigneeId
    ) { tasks, members, sortOrder, assigneeFilter ->
        val today = LocalDate.now().toEpochDay()
        val filtered = if (assigneeFilter == null) {
            tasks
        } else {
            tasks.filter { it.task.assigneeId == assigneeFilter }
        }
        val sorted = when (sortOrder) {
            TaskSortOrder.DUE_DATE -> filtered.sortedBy { it.task.dueDateEpochDay }
            TaskSortOrder.PRIORITY -> filtered.sortedByDescending { it.task.priority.ordinal }
            TaskSortOrder.ASSIGNEE -> filtered.sortedBy { it.assignee?.name ?: "￿" }
        }
        TaskListUiState(
            tasks = sorted,
            members = members,
            sortOrder = sortOrder,
            selectedAssigneeId = assigneeFilter,
            overdueCount = filtered.count {
                it.task.dueDateEpochDay < today && it.task.status != TaskStatus.DONE
            },
            isLoading = false
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = TaskListUiState()
    )

    fun onSortOrderSelected(order: TaskSortOrder) {
        viewModelScope.launch { preferencesRepository.setSortOrder(order) }
    }

    fun onAssigneeFilterSelected(memberId: Long?) {
        selectedAssigneeId.value = memberId
    }

    fun toggleDone(task: Task) {
        viewModelScope.launch {
            val newStatus = if (task.status == TaskStatus.DONE) TaskStatus.TODO else TaskStatus.DONE
            repository.updateTask(task.copy(status = newStatus))
        }
    }
}
