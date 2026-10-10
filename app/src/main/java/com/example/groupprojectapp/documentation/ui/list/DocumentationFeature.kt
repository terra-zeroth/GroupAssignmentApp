package com.example.groupprojectapp.documentation.ui.list

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

@Composable
fun DocumentationFeature(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBackClick)

    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) }

    val factory = remember {
        viewModelFactory {
            initializer {
                DocumentationViewModel(container.documentationRepository)
            }
        }
    }

    val viewModel: DocumentationViewModel = viewModel(factory = factory)
    val uiState by viewModel.uiState.collectAsState()

    DocumentationContent(
        uiState = uiState,
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        modifier = modifier
    )
}