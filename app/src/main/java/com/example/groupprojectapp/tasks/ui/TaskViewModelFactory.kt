package com.example.groupprojectapp.tasks.ui

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groupprojectapp.tasks.TaskContainer

/**
 * SETUP: tells Android how to build our ViewModels. Android can't create a
 * ViewModel that needs constructor arguments by itself, so this factory
 * supplies them from [TaskContainer]. The detail ViewModel also gets a
 * SavedStateHandle, which carries the taskId passed through navigation.
 */

fun taskViewModelFactory(container: TaskContainer) = viewModelFactory {
    initializer {
        TaskListViewModel(
            repository = container.taskRepository,
            preferencesRepository = container.preferencesRepository
        )
    }
    initializer {
        TaskDetailViewModel(
            repository = container.taskRepository,
            savedStateHandle = createSavedStateHandle()
        )
    }
}
