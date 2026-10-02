package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import com.example.groupprojectapp.tasks.ui.TasksFeature

/**
 * Kept as a thin delegate on purpose: MainActivity.kt already calls
 * TasksScreen(userName = userName) and nobody needs to touch that file to
 * pick up everything in the tasks/ package (Room, DataStore, ViewModels,
 * its own internal navigation for the detail-by-id screen). See
 * com.example.groupprojectapp.tasks.ui.TasksNavHost for the real
 * implementation.
 */
@Composable
fun TasksScreen(userName: String) {
    TasksFeature(currentUserName = userName)
}
