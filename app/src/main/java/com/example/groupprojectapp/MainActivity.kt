package com.example.groupprojectapp

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
                    onNavigate = { screen -> sessionViewModel.navigateTo(screen) },
                    onLogout = { sessionViewModel.logout() }
                )

                "Tasks" -> TasksScreen(
                    userName = sessionState.userName,
                    onBackClick = { sessionViewModel.navigateTo("home") }
                )

                "Timeline" -> TimelineScreen(
                    onBackClick = { sessionViewModel.navigateTo("home")}
                )

                "Documentation" -> DocumentationScreen(
                    onBackClick = { sessionViewModel.navigateTo("home") },
                    onAddClick = {
                        sessionViewModel.navigateTo("DocumentationAdd")
                    }
                )

                // new screen for adding new documentation details
                "DocumentationAdd" ->
                    AddDocumentationScreen(
                        onBackClick = { sessionViewModel.navigateTo("Documentation") },
                        onSaved = { sessionViewModel.navigateTo("Documentation") }
                    )


                "Github" -> GithubScreen()
                "Settings" -> SettingsScreen()
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
                ) {
                    Text("Completion:")
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
                ) {
                    Text("Current To-Do")
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