package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.groupprojectapp.tasks.data.TaskStatus

@Composable
fun timelineItemColor(
    isOverdue: Boolean,
    status: TaskStatus
): Color {
    // check if it is overdue like the status text
    if (isOverdue){
        return MaterialTheme.colorScheme.error
    }

    // change the colour of the bars here:
    return when (status){
        TaskStatus.DONE -> MaterialTheme.colorScheme.tertiary
        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
        TaskStatus.TODO -> MaterialTheme.colorScheme.outline
    }

}