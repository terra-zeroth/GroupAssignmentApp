package com.example.groupprojectapp.tasks

import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

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
