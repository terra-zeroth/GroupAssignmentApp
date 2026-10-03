package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.timeline.ui.TimelineItem
import java.time.LocalDate

@Composable
fun TimelineRightPane(
    items: List<TimelineItem>,
    firstDate: LocalDate,
    totalNumDays: Int,
    headerHeight: Dp,
    rowHeight: Dp,
    dayWidth: Dp,
    horizontalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    Column(
        modifier = modifier
            .horizontalScroll(horizontalScrollState)
    ) {
        // Date Header Row
        Row(
            modifier = Modifier.height(headerHeight)
        ) {
            (0 until totalNumDays).forEach { dayIndex ->
                val currentDate = firstDate.plusDays(dayIndex.toLong())
                Box(
                    modifier = Modifier
                        .width(dayWidth)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    // Show just the day number to prevent cramming / wrapping at 2x font scale
                    Text(
                        text = currentDate.dayOfMonth.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }

        // Empty Tracks (One row per task)
        items.forEach { _ ->
            Row(
                modifier = Modifier
                    .height(rowHeight)
                    .border(0.5.dp, outlineColor.copy(alpha = 0.5f))
            ) {
                (0 until totalNumDays).forEach { _ ->
                    Box(
                        modifier = Modifier
                            .width(dayWidth)
                            .fillMaxHeight()
                            .border(0.5.dp, outlineColor.copy(alpha = 0.2f))
                    )
                }
            }
        }
    }
}
