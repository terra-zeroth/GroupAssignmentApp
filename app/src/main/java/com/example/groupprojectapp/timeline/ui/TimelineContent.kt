package com.example.groupprojectapp.timeline.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.components.EmptyTimelineState
import com.example.groupprojectapp.timeline.ui.components.TimelineLeftPane
import com.example.groupprojectapp.timeline.ui.components.TimelineLegend
import com.example.groupprojectapp.timeline.ui.components.TimelineRightPane
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

/*
    TODO:
    [x] date header (Option B: show month name on first column and when day == 1)
    one row per task with
       [x] title
       [x] assignee
       [ ] bar positioned
       [ ] sized
       [ ] use startOffsetDays/lengthDays
       [ ] colour by status (check isOverdue first)
    [x] show status as text/icon too, not just colour for (R5) Accessibility
    [ ] today line
    [x] empty state when there are no tasks
    [x] horizontal scrolling
 */

/*
   Accessibility (R5) — larger system text:
      [x] stop label text wrapping onto extra lines (maxLines = 1 + ellipsis)
      [x] row height, header height and day width are sp-based, so they grow with the user's text size
*/

@Composable
fun TimelineContent(
    uiState: TimelineUiState,
    modifier: Modifier = Modifier
){
    // No scroll state parameter: scroll positions are only used inside this screen,
    // so they're created here with rememberScrollState(). Neither the ViewModel nor
    // the Previews need to see or control them.
    
    val density = LocalDensity.current
    // Dimensions defined in sp so they scale properly with accessibility font size, then converted to Dp
    val rowHeightDp = with(density) { 64.sp.toDp() }
    val headerHeightDp = with(density) { 40.sp.toDp() }
    val dayWidthDp = with(density) { 56.sp.toDp() } // sp-based width so 2x font scale header dates don't wrap/cut off

    // Remember scroll states:
    val verticalScrollState = rememberScrollState()
    val horizontalScrollState = rememberScrollState()

    Column(modifier = modifier.fillMaxSize()) {
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.items.isEmpty() -> {
                EmptyTimelineState()
            }
            else -> {
                // Legend placed inside the chart branch above the scroll container
                TimelineLegend()

                // VERTICAL SCROLL CONTAINER (Links left and right panes together)
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(verticalScrollState)
                ) {
                    // LEFT PANE (Fixed sideways, scrolls vertically)
                    TimelineLeftPane(
                        items = uiState.items,
                        headerHeight = headerHeightDp,
                        rowHeight = rowHeightDp
                    )

                    // RIGHT PANE (Scrolls horizontally & vertically, takes remaining width)
                    TimelineRightPane(
                        items = uiState.items,
                        firstDate = uiState.firstDate,
                        totalNumDays = uiState.totalNumDays,
                        headerHeight = headerHeightDp,
                        rowHeight = rowHeightDp,
                        dayWidth = dayWidthDp,
                        todayOffset = uiState.todayOffset,
                        horizontalScrollState = horizontalScrollState,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

// SHARED SAMPLE DATA FOR PREVIEWS
private val sampleTimelineUiState = TimelineUiState(
    isLoading = false,
    todayOffset = 3,
    firstDate = LocalDate.of(2026, 10, 1),
    totalNumDays = 14,
    items = listOf(
        TimelineItem(
            taskId = 1L,
            title = "Task 1",
            startOffsetDays = 0,
            lengthDays = 1,
            status = TaskStatus.TODO,
            assigneeName = "Aliyah",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 2L,
            title = "Task 2",
            startOffsetDays = 2,
            lengthDays = 3,
            status = TaskStatus.IN_PROGRESS,
            assigneeName = "Twisha",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 3L,
            title = "Task 3",
            startOffsetDays = 1,
            lengthDays = 8,
            status = TaskStatus.DONE,
            assigneeName = "Unassigned",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 4L,
            title = "Task 4",
            startOffsetDays = 0,
            lengthDays = 2,
            status = TaskStatus.TODO,
            assigneeName = "Manasviba",
            isOverdue = true
        )
    )
)

// PREVIEWS ---------------

@Preview(showBackground = true)
@Composable
private fun TimelineContentPreview(){
    GroupProjectAppTheme {
        TimelineContent(uiState = sampleTimelineUiState)
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineContentEmptyPreview(){
    GroupProjectAppTheme{
        TimelineContent(
            uiState = TimelineUiState(
                // test the empty list state, not loading state
                isLoading = false,
                items = emptyList()
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineContentLoadingPreview(){
    GroupProjectAppTheme{
        TimelineContent(
            uiState = TimelineUiState(
                // would show a loading spinner instead of test items
                isLoading = true,
                items = emptyList()
            )
        )
    }
}

@Preview(showBackground = true, name = "Normal Font")
@Preview(showBackground = true, fontScale = 2f, name = "2x Font Scale")
@Composable
private fun TimelineContentSkeletonPreview() {
    GroupProjectAppTheme {
        TimelineContent(uiState = sampleTimelineUiState)
    }
}
