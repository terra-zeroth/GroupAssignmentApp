package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
    todayOffset: Int, // // days from firstDate to today; outside 0 until totalNumDays = line hidden
    horizontalScrollState: ScrollState,
    modifier: Modifier = Modifier
) {
    val outlineColor = MaterialTheme.colorScheme.outlineVariant

    // Outer Box captures horizontal scrolling for
    // header, grid, bars and today line
    Box(
        modifier = modifier.horizontalScroll(horizontalScrollState)
    ) {
        Column {
            // Date Header Row
            Row(
                modifier = Modifier.height(headerHeight)
            ) {
                (0 until totalNumDays).forEach { dayIndex ->
                    val currentDate = firstDate.plusDays(dayIndex.toLong())
                    val isToday = dayIndex == todayOffset // today line calculation

                    // Show month label on first column (dayIndex == 0) OR on 1st of any month
                    val showMonthLabel = dayIndex == 0 || currentDate.dayOfMonth == 1
                    val dateText = if (showMonthLabel) {
                        val monthName = currentDate.month.name.take(3).lowercase().replaceFirstChar { it.uppercase() }
                        "$monthName ${currentDate.dayOfMonth}"
                    } else {
                        currentDate.dayOfMonth.toString()
                    }

                    Box(
                        modifier = Modifier
                            .width(dayWidth)
                            .fillMaxHeight(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.labelSmall,
                            maxLines = 1,
                            color = if (isToday) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            items.forEach { item ->
                // outer box allows layering the bar over the grid boxes
                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(dayWidth * totalNumDays)
                ) {
                    // background day grid row
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

                    // task bar overlay
                    val barColor = timelineItemColor(
                        isOverdue = item.isOverdue,
                        status = item.status
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = dayWidth * item.startOffsetDays)
                            .width(dayWidth * item.lengthDays)
                            .height(rowHeight / 2) // half the height of the row
                            .clip(RoundedCornerShape(4.dp))
                            .background(barColor)
                    )
                }
            }
        }

        // TODAY LINE overlay (drawn after column so it sits on top)
        if (todayOffset in 0 until totalNumDays) {
            val totalCalculatedHeight = headerHeight + (rowHeight * items.size)
            val lineOffset = (dayWidth * todayOffset) + (dayWidth / 2)

            Box(
                modifier = Modifier
                    .offset(x = lineOffset)
                    .width(2.dp)
                    .height(totalCalculatedHeight)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}