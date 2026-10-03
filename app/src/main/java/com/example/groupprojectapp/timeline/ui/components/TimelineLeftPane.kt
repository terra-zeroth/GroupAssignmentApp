package com.example.groupprojectapp.timeline.ui.components

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

@Composable
fun TimelineLeftPane(
    items: List<TimelineItem>,
    headerHeight: Dp,
    rowHeight: Dp,
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
                    val statusText = when {
                        item.isOverdue -> "Overdue"
                        else -> when (item.status) {
                            TaskStatus.TODO -> "To Do"
                            TaskStatus.IN_PROGRESS -> "In Progress"
                            TaskStatus.DONE -> "Done"
                        }
                    }
                    Text(
                        text = "${item.assigneeName} · $statusText",
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
