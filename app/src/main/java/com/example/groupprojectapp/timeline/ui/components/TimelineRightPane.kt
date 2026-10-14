package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.timeline.ui.TimelineItem
import java.time.LocalDate

/** Thickness of the chart's grid lines. Shared with [TimelineLeftPane] so both panes match. */
internal val TimelineGridLineWidth = 1.5.dp

/** Thickness of the red today line. */
private val TodayLineWidth = 3.dp

/**
 * VIEW (MVVM) — timeline component:
 * the right-hand side of the chart: date header, one track per task with its
 * bar, and the today line.
 *
 * Layout: everything sits inside ONE horizontally scrolling Box, so the
 * header, bars and today line always move together. Bars are positioned with
 * offset (dayWidth × startOffsetDays) and sized with width
 * (dayWidth × lengthDays); those day numbers come from [TimelineViewModel].
 *
 * Header labels: day number for each column, plus the month name on the
 * first column and on the 1st of any month. Today's number is shown in red.
 *
 * Grid: navy lines (theme outline) on the right and bottom edge of every day
 * cell, plus a line under the header. Each edge is drawn once, so shared
 * edges don't double in thickness. In each row the grid is drawn after the
 * bar, so the lines sit on top of it (taps still pass through to the bar).
 *
 * Today line: drawn after the Column so it sits on top of bars and grid. It
 * starts below the date header (offset by headerHeight) so it never covers
 * the date numbers, and its height is the rows only. The height is
 * calculated rather than using fillMaxHeight, because this pane sits inside
 * a vertical scroll where the available height is unlimited. Hidden when
 * todayOffset is outside the chart's range.
 *
 * Rows must use the same rowHeight as [TimelineLeftPane] or labels and bars
 * stop lining up.
 */
@Composable
fun TimelineRightPane(
    items: List<TimelineItem>,
    firstDate: LocalDate,
    totalNumDays: Int,
    headerHeight: Dp,
    rowHeight: Dp,
    dayWidth: Dp,
    todayOffset: Int, // days from firstDate to today; outside 0 until totalNumDays = line hidden
    horizontalScrollState: ScrollState,
    onItemClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    // Navy in light mode, periwinkle in dark mode
    val gridColor = MaterialTheme.colorScheme.outline

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
                    val isToday = dayIndex == todayOffset // red header number check

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
                            .fillMaxHeight()
                            .gridCellLines(gridColor, bottom = true), // line under the header
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
                // outer box layers the bar and the grid lines; later children draw on top
                Box(
                    modifier = Modifier
                        .height(rowHeight)
                        .width(dayWidth * totalNumDays)
                ) {
                    // task bar (drawn first, so the grid lines sit on top of it)
                    val barColor = timelineItemColor(
                        isOverdue = item.isOverdue,
                        status = item.status
                    )

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .offset(x = dayWidth * item.startOffsetDays)
                            .width(dayWidth * item.lengthDays)
                            .height(rowHeight * 0.6f) // most of the row, like the design
                            .clip(RoundedCornerShape(2.dp))
                            .clickable(onClickLabel = "Show task details") {
                                onItemClick(item.taskId)
                            }
                            .background(barColor)
                    )

                    // day grid lines (drawn after the bar, so they appear on top of it).
                    // The cells have no clickable, so taps pass through to the bar underneath.
                    Row(
                        modifier = Modifier.height(rowHeight)
                    ) {
                        (0 until totalNumDays).forEach { _ ->
                            Box(
                                modifier = Modifier
                                    .width(dayWidth)
                                    .fillMaxHeight()
                                    .gridCellLines(gridColor, right = true, bottom = true)
                            )
                        }
                    }
                }
            }
        }

        // TODAY LINE overlay (drawn after column so it sits on top of bars and grid)
        // Starts below the date header so it doesn't cover the date numbers;
        // today's number is shown in red instead.
        if (todayOffset in 0 until totalNumDays) {
            val lineHeight = rowHeight * items.size
            // centre of today's column, shifted left by half the line width so the line is centred
            val lineOffset = (dayWidth * todayOffset) + (dayWidth / 2) - (TodayLineWidth / 2)

            Box(
                modifier = Modifier
                    .offset(x = lineOffset, y = headerHeight)
                    .width(TodayLineWidth)
                    .height(lineHeight)
                    .background(MaterialTheme.colorScheme.error)
            )
        }
    }
}

/**
 * Draws grid lines on the chosen edges of a cell.
 * Lines are drawn inside the cell (offset by half the stroke) so they
 * aren't clipped, and each shared edge is only drawn by one cell.
 */
private fun Modifier.gridCellLines(
    color: Color,
    right: Boolean = false,
    bottom: Boolean = false
): Modifier = this.drawBehind {
    val stroke = TimelineGridLineWidth.toPx()
    val half = stroke / 2

    if (right) {
        drawLine(
            color = color,
            start = Offset(size.width - half, 0f),
            end = Offset(size.width - half, size.height),
            strokeWidth = stroke
        )
    }
    if (bottom) {
        drawLine(
            color = color,
            start = Offset(0f, size.height - half),
            end = Offset(size.width, size.height - half),
            strokeWidth = stroke
        )
    }
}