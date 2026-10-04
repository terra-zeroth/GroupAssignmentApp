package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskStatus

/**
 * VIEW (MVVM) — timeline component:
 * the colour key shown above the chart.
 *
 * Swatch colours come from [timelineItemColor], the same function the bars
 * use, so the legend and the bars can never disagree.
 * FlowRow wraps entries onto a new line when space runs out (e.g. with large
 * system text), instead of squeezing the last label.
 */
@Composable
fun TimelineLegend(modifier: Modifier = Modifier) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LegendEntry(
            label = "Done",
            color = timelineItemColor(isOverdue = false, status = TaskStatus.DONE)
        )
        LegendEntry(
            label = "In Progress",
            color = timelineItemColor(isOverdue = false, status = TaskStatus.IN_PROGRESS)
        )
        LegendEntry(
            label = "To Do",
            color = timelineItemColor(isOverdue = false, status = TaskStatus.TODO)
        )
        LegendEntry(
            label = "Overdue",
            color = timelineItemColor(isOverdue = true, status = TaskStatus.TODO)
        )
    }
}

/** One legend item: a small coloured square followed by its label. */
@Composable
private fun LegendEntry(
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Coloured swatch square
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
        )
        // Label text in normal theme colour, maxLines = 1 to prevent text splitting
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            maxLines = 1
        )
    }
}
