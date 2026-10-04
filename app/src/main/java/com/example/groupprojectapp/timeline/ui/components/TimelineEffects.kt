package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.groupprojectapp.tasks.data.TaskStatus

/*
custom color definitions for status bars (Hex values with Light/Dark support)

    (R5): Red and green are hard to tell apart for colour-blind users;
     status is also shown as text in the left pane

 */
private val GreenLight = Color(0xFF2E7D32)
private val GreenDark = Color(0xFF81C784)

private val OrangeLight = Color(0xFFE65100)
private val OrangeDark = Color(0xFFFFB74D)



@Composable
fun timelineItemColor(
    isOverdue: Boolean,
    status: TaskStatus
): Color {
    // in case for any dark theme
    val isDark = isSystemInDarkTheme()


    // check if it is overdue like the status text
    if (isOverdue){
        return MaterialTheme.colorScheme.error
    }

    // change the colour of the bars here:
    return when (status) {
        TaskStatus.DONE -> if (isDark) GreenDark else GreenLight
        TaskStatus.TODO -> if (isDark) OrangeDark else OrangeLight
        TaskStatus.IN_PROGRESS -> MaterialTheme.colorScheme.primary
    }

}