package com.example.groupprojectapp

import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import com.example.groupprojectapp.home.HomeCalculations
import com.example.groupprojectapp.tasks.TaskContainer
import com.example.groupprojectapp.tasks.data.DeadlineCalculations
import com.example.groupprojectapp.timeline.ui.components.timelineItemColor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.groupprojectapp.documentation.ui.add.AddDocumentationScreen
import com.example.groupprojectapp.login.ui.LoginScreen
import com.example.groupprojectapp.session.SessionViewModel

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
                // text colour, so screens without a Scaffold (e.g. HomeScreen) match too
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

    /** The home screen: shows the group name and a button for each section of the app. */
    @Composable
    fun HomeScreen(
        groupName: String,
        userName: String,
        onNavigate: (String) -> Unit,
        onLogout: () -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = groupName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            val context = LocalContext.current
            val container = remember { TaskContainer(context) }
            val tasks by container.taskRepository.allTasks.collectAsState(initial = emptyList())
            val completionPercent = HomeCalculations.completionPercent(tasks, userName)
            val currentTasks = HomeCalculations.currentTasks(tasks, userName)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.5f)
                        .height(120.dp)
                        .border(
                            width = 2.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column {
                        Text("Completion:")
                        Text(
                            text = "$completionPercent%",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(120.dp)
                        .border(
                            width = 2.dp,
                            color = Color.DarkGray,
                            shape = RoundedCornerShape(size = 4.dp)
                        )
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Current To-Do")
                        if (currentTasks.isEmpty()) {
                            Text("Nothing assigned to you", fontSize = 12.sp)
                        } else {
                            currentTasks.forEach { taskWithAssignee ->
                                val rowColor = timelineItemColor(
                                    isOverdue = HomeCalculations.isOverdue(taskWithAssignee),
                                    status = taskWithAssignee.task.status
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = rowColor.copy(alpha = 0.25f),
                                            shape = RoundedCornerShape(size = 3.dp)
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = rowColor,
                                            shape = RoundedCornerShape(size = 3.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = taskWithAssignee.task.title,
                                        fontSize = 12.sp,
                                        maxLines = 1
                                    )
                                }
                            }
                        }
                    }
                }

            }

            Button(
                onClick = { onNavigate("Tasks") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Tasks")
            }

            Button(
                onClick = { onNavigate("Timeline") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Timeline")
            }

            Button(
                onClick = { onNavigate("Documentation") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Documentation")
            }

            Button(
                onClick = { onNavigate("Github") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to GitHub")
            }

            Button(
                onClick = { onNavigate("Settings") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go to Settings")
            }

            Button(
                onClick = { onLogout() }, // Triggers logout to clear state & send back to Login
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go back to Login")
            }


        }

    }



}