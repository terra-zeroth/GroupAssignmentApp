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
import kotlinx.coroutines.flow.first
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
    val dependsOnTaskId: Long? = null,
    val members: List<Member> = emptyList(),
    val availableTasks: List<Task> = emptyList(),
    val isNewTask: Boolean = true,
    val isLoading: Boolean = true,
    val titleError: Boolean = false,
    val startDateError: Boolean = false,
)

/**
 * Holds the whole add/edit form in ViewModel state, not local `remember {}`
 * in the composable. That's what makes R1's rotation requirement pass.
 *
 * BUGFIX: the existing task's fields are now loaded ONCE via
 * repository.taskById(taskId).first() instead of an ongoing .collect{}.
 * The old version stayed subscribed to that task's row for the whole time
 * the form was open, so any background write to it (e.g. our own
 * syncAutoStatuses() auto "In Progress" job firing on rotation) would
 * silently overwrite every field in the form — including unsaved
 * keystrokes in title/description/dates — back to whatever was currently
 * in the database. Loading once means the form only ever reflects what's
 * in the database at the moment it opened, plus whatever the user typed
 * since, and nothing external can clobber it mid-edit.
 *
 * [availableTasks] (for the "depends on" picker) is still a live .collect
 * on purpose — that's just the list of *options* to choose from, not the
 * user's actual field values, so it's fine (and good) for it to update
 * live if another task is added while this form is open.
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
        viewModelScope.launch {
            repository.allTasks.collect { tasksWithAssignee ->
                val others = tasksWithAssignee
                    .map { it.task }
                    .filter { it.id != taskId }
                _uiState.update { it.copy(availableTasks = others) }
            }
        }
        if (taskId != null) {
            viewModelScope.launch {
                val task = repository.taskById(taskId).first()?.task ?: run {
                    _uiState.update { it.copy(isLoading = false) }
                    return@launch
                }
                _uiState.update {
                    it.copy(
                        title = task.title,
                        description = task.description,
                        dueDate = LocalDate.ofEpochDay(task.dueDateEpochDay),
                        startDate = LocalDate.ofEpochDay(task.startDateEpochDay),
                        priority = task.priority,
                        status = task.status,
                        assigneeId = task.assigneeId,
                        dependsOnTaskId = task.dependsOnTaskId,
                        isLoading = false
                    )
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
    fun onDependsOnChange(value: Long?) = _uiState.update { it.copy(dependsOnTaskId = value) }

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
                assigneeId = state.assigneeId,
                dependsOnTaskId = state.dependsOnTaskId
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
