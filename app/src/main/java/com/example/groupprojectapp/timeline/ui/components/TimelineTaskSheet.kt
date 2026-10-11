package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.TimelineItem
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val sheetDateFormat = DateTimeFormatter.ofPattern("d MMM yyyy")

/**
 * VIEW (MVVM) — timeline component:
 * content of the task details bottom sheet, opened by tapping a bar or label.
 *
 * Shows the full title (allowed to wrap, unlike the 1-line chart label),
 * assignee · status, and start → due dates, plus a button to open that
 * task's documentation. Dates come ready-made from [TimelineItem]; this
 * only formats them for display.
 *
 * Kept separate from the ModalBottomSheet in TimelineContent so it can be
 * previewed on its own.
 */
@Composable
fun TimelineTaskSheet(
    item: TimelineItem,
    onOpenDocsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 32.dp)
    ) {
        // Full title: no maxLines, so long titles wrap here
        Text(
            text = item.title,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "${item.assigneeName} · ${timelineStatusText(item)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "${item.startDate.format(sheetDateFormat)} → ${item.dueDate.format(sheetDateFormat)}",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onOpenDocsClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Open documentation")
        }
    }
}

@Preview(showBackground = true, name = "Task Sheet")
@Preview(showBackground = true, fontScale = 2f, name = "Task Sheet - 2x Font")
@Composable
private fun TimelineTaskSheetPreview() {
    GroupProjectAppTheme {
        TimelineTaskSheet(
            item = TimelineItem(
                taskId = 3L,
                startDate = LocalDate.of(2026, 10, 2),
                dueDate = LocalDate.of(2026, 10, 9),
                title = "A very long task title that would be cut off on the chart but wraps here",
                assigneeName = "Twisha",
                status = TaskStatus.IN_PROGRESS
            ),
            onOpenDocsClick = {}
        )
    }
}