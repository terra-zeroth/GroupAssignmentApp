package com.example.groupprojectapp.tasks

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val assigneeId: Long? = null,
    val members: List<Member> = emptyList(),
    val isNewTask: Boolean = true,
    val isLoading: Boolean = true,
    val titleError: Boolean = false
)

/**
 * Holds the whole add/edit form in ViewModel state, not local `remember {}`
 * in the composable. That's what makes R1's rotation requirement pass: the
 * Activity recreates on rotation, but this ViewModel instance survives
 * (it's scoped to this NavBackStackEntry via Navigation-Compose), so every
 * field the user typed is still here afterwards — nothing extra has to be
 * done to "handle" rotation, it falls out of where the state lives.
 *
 * NOTE for the group: this only fixes rotation *inside* the Tasks feature.
 * MainActivity's own currentScreen/groupName/userName use plain
 * `remember { mutableStateOf(...) }`, which does NOT survive rotation —
 * that resets the whole app back to the Login screen on rotate. That's a
 * 3-line fix (remember -> rememberSaveable) but it's in MainActivity.kt,
 * so it needs whoever owns that file to make it.
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
                            priority = task.priority,
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
    fun onDueDateChange(value: LocalDate) = _uiState.update { it.copy(dueDate = value) }
    fun onPriorityChange(value: TaskPriority) = _uiState.update { it.copy(priority = value) }
    fun onAssigneeChange(value: Long?) = _uiState.update { it.copy(assigneeId = value) }

    fun saveTask(onSaved: () -> Unit) {
        val state = _uiState.value
        if (state.title.isBlank()) {
            _uiState.update { it.copy(titleError = true) }
            return
        }
        viewModelScope.launch {
            val task = Task(
                id = taskId ?: 0,
                title = state.title.trim(),
                description = state.description.trim(),
                dueDateEpochDay = state.dueDate.toEpochDay(),
                priority = state.priority,
                assigneeId = state.assigneeId
            )
            if (taskId == null) repository.saveTask(task) else repository.updateTask(task)
            onSaved()
        }
    }
}
