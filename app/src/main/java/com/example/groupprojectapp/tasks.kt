package com.example.groupprojectapp

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.groupprojectapp.SampleData.sampleTasks

@Composable
fun TasksScreen(userName: String) {
    val tasks = sampleTasks[userName] ?: emptyList()

    Column {
        Text(
            text = "Tasks for $userName",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )
        tasks.forEach { task ->
            Text("• $task")
        }
    }
}