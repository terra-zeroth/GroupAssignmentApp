package com.example.groupprojectapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.layout.fillMaxWidth

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var currentScreen by remember { mutableStateOf("Login") }
            var groupName by remember { mutableStateOf("") } // Used state hoisting for the group name and user's name
            var userName by remember { mutableStateOf("") }

            when (currentScreen) {
                "home" -> HomeScreen(
                    groupName = groupName,
                    userName = userName,
                    onNavigate = { screen -> currentScreen = screen }
                )

                "Tasks" -> TasksScreen(
                    userName = userName
                )
                "Timeline" -> TimelineScreen()
                "Documentation" -> DocumentationScreen()
                "Github" -> GithubScreen()
                "Settings" -> SettingsScreen()
                "Login" -> LoginScreen(
                    groupName = groupName,
                    userName = userName,
                    onGroupNameChange = { groupName = it },
                    onUserNameChange = { userName = it},
                    onNavigate = { screen -> currentScreen = screen }
                )
            }
        }

    }


    @Composable
    fun HomeScreen(
        groupName: String,
        userName: String,
        onNavigate: (String) -> Unit
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = "$groupName",
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
                onClick = { onNavigate("Login") },
                shape = RectangleShape,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Go back to Login")
            }
        }
    }
}
