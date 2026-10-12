package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.TimelineItem

/**
 * VIEW (MVVM) — timeline component:
 * the fixed left-hand column of task labels (it scrolls up and down with the
 * chart but never sideways).
 *
 * Each row shows the title on line 1 and "assignee status" on line 2.
 * Status is written out (including "Overdue", checked first) so colour isn't
 * the only signal (R5).
 *
 * Alignment: the top spacer matches the date header height, and every row
 * uses the same rowHeight as [TimelineRightPane], so each label sits level
 * with its bar. Both lines are limited to one line with an ellipsis so long
 * titles or large system text can't make a row taller.
 */
@Composable
fun TimelineLeftPane(
    items: List<TimelineItem>,
    headerHeight: Dp,
    rowHeight: Dp,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.width(180.dp)) {
        // Blank Spacer to align with Date Header in right pane
        Spacer(modifier = Modifier.height(headerHeight))

        // Task Labels (2 lines: Title on line 1, Assignee · Status on line 2)
        items.forEach { item ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(rowHeight)
                    .clickable(onClickLabel = "Show task details") {
                        onItemClick(item.taskId)
                    }
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column {
                    Text(
                        text = item.title,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(2.dp))

                    // Uses the shared helper function instead of duplicated logic
                    Text(
                        text = "${item.assigneeName} · ${timelineStatusText(item)}",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Converts a task item into a human-readable status string.
 * Checks if the task is overdue first (overriding normal progress),
 * and formats remaining statuses into explicit text to satisfy accessibility
 * requirements where color is never the only signal (R5).
 */
fun timelineStatusText(item: TimelineItem): String = when {
    item.isOverdue -> "Overdue"
    else -> when (item.status) {
        TaskStatus.TODO -> "To Do"
        TaskStatus.IN_PROGRESS -> "In Progress"
        TaskStatus.DONE -> "Done"
    }
}
