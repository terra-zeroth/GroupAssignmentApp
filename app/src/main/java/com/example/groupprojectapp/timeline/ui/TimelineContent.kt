package com.example.groupprojectapp.timeline.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.components.EmptyTimelineState
import com.example.groupprojectapp.timeline.ui.components.TimelineLeftPane
import com.example.groupprojectapp.timeline.ui.components.TimelineLegend
import com.example.groupprojectapp.timeline.ui.components.TimelineRightPane
import com.example.groupprojectapp.timeline.ui.components.TimelineTaskSheet
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

/**
 * VIEW (MVVM):
 * the Timeline (Gantt chart) screen.
 *
 * What it shows: a top bar with Back, then one of three states:
 * loading spinner, empty message, or the chart (legend, a fixed left pane of
 * task labels, and a right pane with the date header, bars and today line
 * that scrolls sideways).
 *
 * What it does NOT do: no date maths, filtering or database work. It draws
 * [TimelineUiState] and passes the Back click up through onBackClick.
 * Because it only receives state, the previews below can supply fake data.
 *
 * Local UI-only state: the two scroll positions stay here because they are
 * not app data and nothing outside this screen needs them.
 *
 * Accessibility (R5): row height, header height and day width are sp-based
 * so the chart grows with the user's text size; status is shown as text as
 * well as colour; the Back icon has a content description.
 */

/*
    TODO (MAKE SURE TO REMOVE THIS - BEFORE SUBMISSION):
    [x] date header (Option B: show month name on first column and when day == 1)
    one row per task with
       [x] title
       [x] assignee
       [x] bar positioned
       [x] sized
       [x] use startOffsetDays/lengthDays
       [x] colour by status (check isOverdue first)
    [x] show status as text/icon too, not just colour for (R5) Accessibility
    [x] today line
    [x] empty state when there are no tasks
    [x] horizontal scrolling
 */

/*
   Accessibility (R5) — larger system text:
      [x] stop label text wrapping onto extra lines (maxLines = 1 + ellipsis)
      [x] row height, header height and day width are sp-based, so they grow with the user's text size
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineContent(
    uiState: TimelineUiState,
    onBackClick: () -> Unit,
    onOpenDocs: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    // UI-only state: which task's details sheet is open (null = closed).
    // rememberSaveable so it survives rotation, like activePicker in the task form.
    var sheetTaskId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Timeline") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Dashboard"
                        )
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        // No scroll state parameter: scroll positions are only used inside this screen,
        // so they're created here with rememberScrollState(). Neither the ViewModel nor
        // the Previews need to see or control them.

        val density = LocalDensity.current
        // Dimensions defined in sp so they scale properly with accessibility font size, then converted to Dp
        val rowHeightDp = with(density) { 64.sp.toDp() }
        val headerHeightDp = with(density) { 40.sp.toDp() }
        val dayWidthDp =
            with(density) { 56.sp.toDp() } // sp-based width so 2x font scale header dates don't wrap/cut off

        // Remember scroll states:
        val verticalScrollState = rememberScrollState()
        val horizontalScrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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

                    // Hint so users know the bars and labels are tappable
                    Text(
                        text = "Tap a task or its bar to see details and open its documentation.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

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
                            rowHeight = rowHeightDp,
                            onItemClick = { taskId -> sheetTaskId = taskId }
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
                            onItemClick = { taskId -> sheetTaskId = taskId },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // TASK DETAILS SHEET
        // Find the tapped task. If it was deleted while open, nothing is found
        // and the sheet simply disappears, with no crash.
        val sheetItem = uiState.items.find { it.taskId == sheetTaskId }

        sheetItem?.let { item ->
            ModalBottomSheet(
                onDismissRequest = { sheetTaskId = null } // swipe down / tap outside
            ) {
                TimelineTaskSheet(
                    item = item,
                    onOpenDocsClick = {
                        sheetTaskId = null // close first...
                        onOpenDocs(item.taskId) // ...then navigate
                    }
                )
            }
        }


    }
}

// SHARED SAMPLE DATA FOR PREVIEWS
// Dates match each item's offset from firstDate (1 Oct): offset 0 = 1 Oct
private val sampleTimelineUiState = TimelineUiState(
    isLoading = false,
    todayOffset = 3,
    firstDate = LocalDate.of(2026, 10, 1),
    totalNumDays = 14,
    items = listOf(
        TimelineItem(
            taskId = 1L,
            startDate = LocalDate.of(2026, 10, 1),     // ← new (offset 0)
            dueDate = LocalDate.of(2026, 10, 1),       // ← new (length 1)
            title = "Task 1",
            startOffsetDays = 0,
            lengthDays = 1,
            status = TaskStatus.TODO,
            assigneeName = "Aliyah",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 2L,
            startDate = LocalDate.of(2026, 10, 3),     // ← new (offset 2)
            dueDate = LocalDate.of(2026, 10, 5),       // ← new (length 3)
            title = "Task 2",
            startOffsetDays = 2,
            lengthDays = 3,
            status = TaskStatus.IN_PROGRESS,
            assigneeName = "Twisha",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 3L,
            startDate = LocalDate.of(2026, 10, 2),     // ← new (offset 1)
            dueDate = LocalDate.of(2026, 10, 9),       // ← new (length 8)
            title = "Task 3",
            startOffsetDays = 1,
            lengthDays = 8,
            status = TaskStatus.DONE,
            assigneeName = "Unassigned",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 4L,
            startDate = LocalDate.of(2026, 10, 1),     // ← new (offset 0)
            dueDate = LocalDate.of(2026, 10, 2),       // ← new (length 2)
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


// simultanously shows both dark and light theme
@Preview(showBackground = true, name = "Light Mode")
@Preview(
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    name = "Dark Mode"
)
@Composable
private fun TimelineContentThemePreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState,
            onBackClick = {},
            onOpenDocs = {}
        )
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
            ),
            onBackClick = {},
            onOpenDocs = {}
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
            ),
            onBackClick = {},
            onOpenDocs = {}
        )
    }
}

@Preview(showBackground = true, name = "Normal Font")
@Preview(showBackground = true, fontScale = 2f, name = "2x Font Scale")
@Composable
private fun TimelineContentSkeletonPreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState,
            onBackClick = {},
            onOpenDocs = {}
        )
    }
}