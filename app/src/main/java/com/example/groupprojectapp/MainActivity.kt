package com.example.groupprojectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groupprojectapp.documentation.ui.add.AddDocumentationScreen
import com.example.groupprojectapp.home.HomeCalculations
import com.example.groupprojectapp.home.HomeContent
import com.example.groupprojectapp.home.HomeTaskItem
import com.example.groupprojectapp.login.ui.LoginScreen
import com.example.groupprojectapp.session.SessionViewModel
import com.example.groupprojectapp.tasks.TaskContainer
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme

/**
 * Entry point of the app. It acts as a simple screen switcher driven by [SessionViewModel].
 *
 * The [SessionViewModel] maintains top-level application state (currentScreen, groupName,
 * userName) across configuration changes such as screen rotations.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // App-wide theme: every screen gets the app palette (colours from ui/theme)
            GroupProjectAppTheme {
                // Surface paints the theme background behind every screen and sets the default
                // text colour, so screens without a Scaffold match too
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 1. Get the shared SessionViewModel and collect its state ABOVE the when block
                    val sessionViewModel: SessionViewModel = viewModel()
                    val sessionState by sessionViewModel.uiState.collectAsState()

                    // 2. Switch screens based on currentScreen from SessionViewModel
                    when (sessionState.currentScreen) {
                        "Login" -> {
                            LoginScreen(
                                onSuccess = { group, user ->
                                    sessionViewModel.onLoginSuccess(group, user)
                                }
                            )
                        }

                        "home" -> HomeScreen(
                            groupName = sessionState.groupName,
                            userName = sessionState.userName,
                            onNavigate = { screen ->
                                if (screen == "Documentation"){
                                    // clear filter, back to home
                                    sessionViewModel.openAllDocumentation()
                                } else {
                                    sessionViewModel.navigateTo(screen)
                                }
                            },
                            onLogout = { sessionViewModel.logout() }
                        )

                        "Tasks" -> TasksScreen(
                            userName = sessionState.userName,
                            onBackClick = { sessionViewModel.navigateTo("home") }
                        )

                        "Timeline" -> TimelineScreen(
                            onBackClick = { sessionViewModel.navigateTo("home")},
                            onOpenDocs = { taskId ->
                                sessionViewModel.openDocumentationForTask(taskId)
                            }
                        )

                        "Documentation" -> DocumentationScreen(
                            taskId = sessionState.selectedTaskId,
                            onBackClick = { sessionViewModel.navigateTo(sessionState.docsBackTarget) },
                            onAddClick = {
                                // sessionViewModel.navigateTo("DocumentationAdd")

                                // clear any old ID and open empty form for a new entry
                                sessionViewModel.openDocumentationEditor(null)
                            },
                            onEntryClick = { entryId ->
                                // Pass the tapped entry's ID to load it into the editor
                                sessionViewModel.openDocumentationEditor(entryId)
                            }
                        )

                        // new screen for adding new documentation details
                        "DocumentationAdd" ->
                            AddDocumentationScreen(
                                entryId = sessionState.selectedEntryId,
                                onBackClick = { sessionViewModel.navigateTo("Documentation") },
                                onSaved = { sessionViewModel.navigateTo("Documentation") },
                                currentUsername = sessionState.userName,
                            )


                        "Github" -> GithubScreen()
                        "Settings" -> SettingsScreen()
                    }
                }
            }
        }
    }

    /**
     * WRAPPER for the Home dashboard: reads the tasks from the database,
     * works out the user's completion and current tasks (HomeCalculations),
     * and passes plain values to [HomeContent], which holds all the UI and
     * can be previewed.
     */
    @Composable
    fun HomeScreen(
        groupName: String,
        userName: String,
        onNavigate: (String) -> Unit,
        onLogout: () -> Unit
    ) {
        val context = LocalContext.current
        val container = remember { TaskContainer(context) }
        val tasks by container.taskRepository.allTasks.collectAsState(initial = emptyList())

        val completionPercent = HomeCalculations.completionPercent(tasks, userName)

        // Convert each task into what one to-do row needs (title, status, overdue)
        val currentTasks = HomeCalculations.currentTasks(tasks, userName).map { taskWithAssignee ->
            HomeTaskItem(
                title = taskWithAssignee.task.title,
                status = taskWithAssignee.task.status,
                isOverdue = HomeCalculations.isOverdue(taskWithAssignee)
            )
        }

        HomeContent(
            groupName = groupName,
            userName = userName,
            completionPercent = completionPercent,
            currentTasks = currentTasks,
            onNavigate = onNavigate,
            onLogout = onLogout
        )
    }
}