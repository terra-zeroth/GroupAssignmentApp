package com.example.groupprojectapp.timeline.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.groupprojectapp.tasks.TaskContainer


/**
 * Wrapper composable responsible for managing dependency injection,
 * ViewModel instantiation, and state collection for the Timeline screen.
 */
@Composable
fun TimelineFeature(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
){
    BackHandler(onBack = onBackClick) // backButton

    // obtain the application context to initialize local storage/database containers
    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) } // instantiate to access the repository

    val factory = remember {
        viewModelFactory {
            initializer {
                //pass taskRepository from container into viewModel constructor
                TimelineViewModel(container.taskRepository)
            }
        }
    }

    val viewModel: TimelineViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState() // to trigger recompositions on updates

    // pass the collected UI state to the pure UI composable
    TimelineContent(
        uiState = uiState,
        onBackClick = onBackClick,
        modifier = modifier
    )
}
