package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskStatus

/**
 * VIEW (MVVM) — timeline component:
 * the colour key shown above the chart, which is also the status filter.
 *
 * Each status is a FilterChip with its colour swatch, so one row explains
 * the colours AND filters the chart. Tapping a chip sends the category up
 * through onToggle; the ViewModel decides what's shown.
 *
 * Swatch colours come from [timelineItemColor], the same function the bars
 * use, so the legend and the bars can never disagree.
 *
 * Accessibility (R5): selected vs unselected isn't colour-only. Ticked chips
 * have a filled swatch, unticked ones a hollow outline, and FilterChip
 * announces its selected state to TalkBack.
 * FlowRow wraps chips onto a new line when space runs out (e.g. with large
 * system text), instead of squeezing the last label.
 */
@Composable
fun TimelineLegend(
    selectedCategories: Set<TimelineStatusCategory>,
    onToggle: (TimelineStatusCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        TimelineStatusCategory.entries.forEach { category ->
            val isSelected = category in selectedCategories

            FilterChip(
                selected = isSelected,
                onClick = { onToggle(category) },
                label = { Text(text = category.label, maxLines = 1) },
                leadingIcon = {
                    LegendSwatch(
                        color = timelineCategoryColor(category),
                        filled = isSelected
                    )
                }
            )
        }
    }
}

/**
 * Small coloured square: filled when the status is shown,
 * hollow outline when it's hidden (a shape cue, not just colour).
 */
@Composable
private fun LegendSwatch(
    color: Color,
    filled: Boolean,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(2.dp)
    Box(
        modifier = modifier
            .size(12.dp)
            .clip(shape)
            .then(
                if (filled) Modifier.background(color)
                else Modifier.border(2.dp, color, shape)
            )
    )
}

/**
 * Maps a filter category to its bar colour using [timelineItemColor],
 * so the legend always matches the bars.
 */
@Composable
private fun timelineCategoryColor(category: TimelineStatusCategory): Color = when (category) {
    TimelineStatusCategory.DONE -> timelineItemColor(isOverdue = false, status = TaskStatus.DONE)
    TimelineStatusCategory.IN_PROGRESS -> timelineItemColor(isOverdue = false, status = TaskStatus.IN_PROGRESS)
    TimelineStatusCategory.TODO -> timelineItemColor(isOverdue = false, status = TaskStatus.TODO)
    TimelineStatusCategory.OVERDUE -> timelineItemColor(isOverdue = true, status = TaskStatus.TODO)
}