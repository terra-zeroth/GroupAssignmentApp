package com.example.groupprojectapp.tasks.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.groupprojectapp.tasks.data.Member
import com.example.groupprojectapp.tasks.data.Task
import com.example.groupprojectapp.tasks.data.TaskPriority
import com.example.groupprojectapp.tasks.data.TaskRepository
import com.example.groupprojectapp.tasks.data.TaskStatus
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

data class TaskDetailUiState(
    val taskId: Long? = null,
    val title: String = "",
    val description: String = "",
    val dueDate: LocalDate = LocalDate.now(),
    val startDate: LocalDate = LocalDate.now(),
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.TODO,
    val assigneeId: Long? = null,
    val members: List<Member> = emptyList(),
    val isNewTask: Boolean = true,
    val isLoading: Boolean = true,
    val titleError: Boolean = false,
    val startDateError: Boolean = false,
)

/**
 * Holds the whole add/edit form in ViewModel state, not local `remember {}`
 * in the composable. That's what makes R1's rotation requirement pass: the
 * Activity recreates on rotation, but this ViewModel instance survives
 * (it's scoped to this NavBackStackEntry via Navigation-Compose), so every
 * field the user typed is still here afterwards — nothing extra has to be
 * done to "handle" rotation, it falls out of where the state lives.
 *
 * BUGFIX: [status] is now tracked in [TaskDetailUiState] and loaded from
 * the existing task when editing. Previously saveTask() built a new [Task]
 * without passing status at all, so it silently fell back to the Task
 * entity's default (TODO) on every edit — meaning marking something
 * IN_PROGRESS or DONE and then editing its title/description would wipe
 * that status back to TODO. New tasks still correctly start at TODO.
 */
class TaskDetailViewModel(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val taskId: Long? = savedStateHandle.get<Long>(TaskRoutes.DETAIL_ARG)
        ?.takeIf { it != TaskRoutes.NEW_TASK_ID }

    private val _uiState = MutableStateFlow(
        TaskDetailUiState(taskId = taskId, isNewTask = taskId == null)
    )
    val uiState: StateFlow<TaskDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.allMembers.collect { members ->
                _uiState.update { it.copy(members = members) }
            }
        }
        if (taskId != null) {
            viewModelScope.launch {
                repository.taskById(taskId).collect { taskWithAssignee ->
                    val task = taskWithAssignee?.task ?: return@collect
                    _uiState.update {
                        it.copy(
                            title = task.title,
                            description = task.description,
                            dueDate = LocalDate.ofEpochDay(task.dueDateEpochDay),
                            startDate = LocalDate.ofEpochDay(task.startDateEpochDay),
                            priority = task.priority,
                            status = task.status,
                            assigneeId = task.assigneeId,
                            isLoading = false
                        )
                    }
                }
            }
        } else {
            _uiState.update { it.copy(isLoading = false) }
        }
    }

    fun onTitleChange(value: String) = _uiState.update { it.copy(title = value, titleError = false) }
    fun onDescriptionChange(value: String) = _uiState.update { it.copy(description = value) }
    fun onDueDateChange(value: LocalDate) =
        _uiState.update { it.copy(dueDate = value, startDateError = false) }
    fun onStartDateChange(value: LocalDate) =
        _uiState.update { it.copy(startDate = value, startDateError = false) }
    fun onPriorityChange(value: TaskPriority) = _uiState.update { it.copy(priority = value) }
    fun onAssigneeChange(value: Long?) = _uiState.update { it.copy(assigneeId = value) }

    fun saveTask(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = true) }
            return
        }

        if (state.startDate.isAfter(state.dueDate)) {
            _uiState.update { it.copy(startDateError = true) }
            return
        }

        viewModelScope.launch {
            val task = Task(
                id = taskId ?: 0,
                title = state.title.trim(),
                description = state.description.trim(),
                dueDateEpochDay = state.dueDate.toEpochDay(),
                startDateEpochDay = state.startDate.toEpochDay(),
                status = state.status,
                priority = state.priority,
                assigneeId = state.assigneeId
            )
            if (taskId == null){
                repository.saveTask(task)
            } else {
                repository.updateTask(task)
            }
            onSaved()
        }
    }
}
