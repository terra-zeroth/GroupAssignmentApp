package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.ui.theme.StatusDone
import com.example.groupprojectapp.ui.theme.StatusInProgress
import com.example.groupprojectapp.ui.theme.StatusTodo

/*
    Status bar colours come from the app palette (ui/theme/Color.kt).
    Done, In progress and To do use the same pastel in light and dark mode;
    Overdue uses the theme's error colour.

    (R5): Red and green are hard to tell apart for colour-blind users;
     status is also shown as text in the left pane and on the legend chips.
 */

/**
 * Returns the bar colour for a task: Overdue = theme error (red),
 * Done = green, In progress = yellow, To do = coral.
 *
 * isOverdue is checked before status, matching the status text in
 * [TimelineLeftPane], so an overdue To-do task is red rather than coral.
 *
 * Used by both the bars and the legend. Composable because Overdue reads
 * MaterialTheme.
 */
@Composable
fun timelineItemColor(
    isOverdue: Boolean,
    status: TaskStatus
): Color {
    // check if it is overdue first, like the status text
    if (isOverdue) {
        return MaterialTheme.colorScheme.error
    }

    return when (status) {
        TaskStatus.DONE -> StatusDone
        TaskStatus.IN_PROGRESS -> StatusInProgress
        TaskStatus.TODO -> StatusTodo
    }
}