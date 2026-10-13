package com.example.groupprojectapp.timeline.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.components.EmptyTimelineState
import com.example.groupprojectapp.timeline.ui.components.TimelineLeftPane
import com.example.groupprojectapp.timeline.ui.components.TimelineLegend
import com.example.groupprojectapp.timeline.ui.components.TimelineRightPane
import com.example.groupprojectapp.timeline.ui.components.TimelineStatusCategory
import com.example.groupprojectapp.timeline.ui.components.TimelineTaskSheet
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

/**
 * VIEW (MVVM):
 * the Timeline (Gantt chart) screen.
 *
 * What it shows: a top bar with Back (and a "Today" button when the chart is
 * visible), then one of four states: loading spinner, empty message (no tasks
 * at all), "no tasks match this filter", or the chart (legend/filter chips,
 * a left pane of task labels, and a right pane with the date header, bars
 * and today line that scrolls sideways).
 *
 * The legend doubles as the status filter. It stays visible when the filter
 * hides everything, so the user can always tick a status again.
 *
 * What it does NOT do: no date maths, filtering or database work. It draws
 * [TimelineUiState] and passes clicks up (onBackClick, onOpenDocs,
 * onFilterToggle). Because it only receives state, the previews below can
 * supply fake data.
 *
 * Sizing: left pane width and day width come from the screen size
 * (BoxWithConstraints). Portrait shows about 4 days; landscape tries to fit
 * the whole project. The day width has an sp-based minimum, so long projects
 * and large text scroll instead of squashing.
 *
 * Scrolling: on opening (and after rotation) the chart scrolls so today is
 * centred in the right pane. The "Today" button re-centres it on demand.
 *
 * Local UI-only state: the scroll positions, the open sheet and the
 * re-centre counter stay here because they are not app data and nothing
 * outside this screen needs them. The filter is app state, so it lives in
 * the ViewModel.
 *
 * Accessibility (R5): row height, header height and minimum day width are
 * sp-based so the chart grows with the user's text size; status is shown as
 * text as well as colour; filter chips use a filled/hollow swatch as well as
 * colour; the Back icon has a content description.
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
    [x] sizes from the screen (BoxWithConstraints)
    [x] auto-scroll to today + Today button
    [x] status filter (legend as filter chips)
 */

/*
   Accessibility (R5) — larger system text:
      [x] stop label text wrapping onto extra lines (maxLines = 1 + ellipsis)
      [x] row height, header height and minimum day width are sp-based, so they grow with the user's text size
*/

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimelineContent(
    uiState: TimelineUiState,
    onBackClick: () -> Unit,
    onOpenDocs: (Long) -> Unit,
    onFilterToggle: (TimelineStatusCategory) -> Unit,
    modifier: Modifier = Modifier
){
    // UI-only state: which task's details sheet is open (null = closed).
    // rememberSaveable so it survives rotation, like activePicker in the task form.
    var sheetTaskId by rememberSaveable { mutableStateOf<Long?>(null) }

    // UI-only state: how many times "Today" has been tapped.
    // The top bar can't see the scroll target (it's worked out inside BoxWithConstraints),
    // so the button just bumps this counter, and the LaunchedEffect below re-runs when it changes.
    var recentreRequests by remember { mutableIntStateOf(0) }

    // Only show the Today button when there is a chart to scroll
    val showChart = !uiState.isLoading && uiState.items.isNotEmpty()

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
                },
                actions = {
                    if (showChart) {
                        // Text button: clearer than an icon and needs no contentDescription
                        TextButton(onClick = { recentreRequests++ }) {
                            Text("Today")
                        }
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
        // Smallest a day column may get. sp-based so 2x font scale header dates don't wrap/cut off.
        // If the screen-based width would be smaller, this wins and the chart scrolls instead.
        val minDayWidthDp = with(density) { 56.sp.toDp() }

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

                // No tasks at all (before filtering)
                uiState.totalTaskCount == 0 -> {
                    EmptyTimelineState()
                }

                // Tasks exist: always show the legend/filter so the user can change it
                else -> {
                    TimelineLegend(
                        selectedCategories = uiState.selectedCategories,
                        onToggle = onFilterToggle
                    )

                    // Hint line 1: how to use the legend
                    Text(
                        text = "Tap a status above to show or hide it.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
                    )

                    if (uiState.items.isEmpty()) {
                        // Tasks exist, but the filter hides all of them
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No tasks match this filter.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        // Hint line 2: only shown when there's something to tap
                        Text(
                            text = "Tap a task or its bar for details and documentation.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 4.dp)
                        )

                        // SCREEN-BASED SIZES
                        // BoxWithConstraints tells us how much space the chart has (maxWidth/maxHeight, already Dp)
                        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                            val isLandscape = maxWidth > maxHeight

                            // Left pane: a share of the width, kept within sensible bounds
                            val leftPaneWidthDp = (maxWidth * if (isLandscape) 0.25f else 0.35f)
                                .coerceIn(110.dp, 220.dp)

                            // Right pane gets whatever is left
                            val rightPaneWidthDp = maxWidth - leftPaneWidthDp

                            // Portrait: ~4 days visible. Landscape: try to fit the whole project.
                            // coerceAtLeast(1) guards against dividing by 0 days.
                            val dayWidthDp = (
                                    if (isLandscape) rightPaneWidthDp / uiState.totalNumDays.coerceAtLeast(1)
                                    else rightPaneWidthDp / 4
                                    ).coerceAtLeast(minDayWidthDp)

                            // AUTO-SCROLL TO TODAY
                            // Today's centre, then shift left by half the visible width so today sits in the middle
                            val todayCentreDp = dayWidthDp * uiState.todayOffset + dayWidthDp / 2
                            val targetDp = todayCentreDp - rightPaneWidthDp / 2

                            // Scroll state works in whole pixels.
                            // Negative / too-large values are fine: scrollTo stops at the edges.
                            val targetPx = with(density) { targetDp.toPx() }.toInt()

                            // Keys:
                            // - targetPx: changes with day width, todayOffset or pane width (e.g. rotation)
                            // - recentreRequests: changes on each "Today" tap
                            // NOT keyed on items: the list re-emits whenever any task (or the filter) changes,
                            // which would yank the chart back to today while the user is looking elsewhere.
                            LaunchedEffect(targetPx, recentreRequests) {
                                horizontalScrollState.scrollTo(targetPx)
                            }

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
                                    width = leftPaneWidthDp,
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
            }
        }

        // TASK DETAILS SHEET
        // Find the tapped task. If it was deleted (or filtered out) while open, nothing is found
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
// totalTaskCount must match items.size, or the screen shows the "no tasks" state
private val sampleTimelineUiState = TimelineUiState(
    isLoading = false,
    todayOffset = 3,
    firstDate = LocalDate.of(2026, 10, 1),
    totalNumDays = 14,
    totalTaskCount = 4,
    items = listOf(
        TimelineItem(
            taskId = 1L,
            startDate = LocalDate.of(2026, 10, 1),
            dueDate = LocalDate.of(2026, 10, 1),
            title = "Task 1",
            startOffsetDays = 0,
            lengthDays = 1,
            status = TaskStatus.TODO,
            assigneeName = "Aliyah",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 2L,
            startDate = LocalDate.of(2026, 10, 3),
            dueDate = LocalDate.of(2026, 10, 5),
            title = "Task 2",
            startOffsetDays = 2,
            lengthDays = 3,
            status = TaskStatus.IN_PROGRESS,
            assigneeName = "Twisha",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 3L,
            startDate = LocalDate.of(2026, 10, 2),
            dueDate = LocalDate.of(2026, 10, 9),
            title = "Task 3",
            startOffsetDays = 1,
            lengthDays = 8,
            status = TaskStatus.DONE,
            assigneeName = "Unassigned",
            isOverdue = false
        ),
        TimelineItem(
            taskId = 4L,
            startDate = LocalDate.of(2026, 10, 1),
            dueDate = LocalDate.of(2026, 10, 2),
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
// Note: Previews don't run LaunchedEffect scrolling reliably — check auto-scroll on the emulator.
// Filter chips don't do anything in Previews (onFilterToggle = {}); test filtering on the emulator.


// simultaneously shows both dark and light theme
// (portrait default size: right pane should show about 4 days)
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
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}

// Landscape: width > height, so day width = right pane ÷ total days (but never below the minimum).
// 800 wide: 600 / 14 ≈ 43 dp < 56 dp minimum → minimum wins, chart scrolls.
// 1100 wide: left clamps to 220, 880 / 14 ≈ 63 dp → whole 14-day sample fits.
@Preview(showBackground = true, widthDp = 800, heightDp = 400, name = "Landscape 800 (scrolls)")
@Preview(showBackground = true, widthDp = 1100, heightDp = 450, name = "Landscape 1100 (fits)")
@Composable
private fun TimelineContentLandscapePreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState,
            onBackClick = {},
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}

// Filter: Done unticked (hollow swatch), so Task 3 is hidden but the dates stay the same
@Preview(showBackground = true, name = "Filter: Done hidden")
@Composable
private fun TimelineContentFilteredPreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState.copy(
                items = sampleTimelineUiState.items.filter { it.status != TaskStatus.DONE },
                selectedCategories = TimelineStatusCategory.entries.toSet() - TimelineStatusCategory.DONE
            ),
            onBackClick = {},
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}

// Filter: everything unticked → "No tasks match this filter", chips still visible
@Preview(showBackground = true, name = "Filter: no matches")
@Composable
private fun TimelineContentNoMatchesPreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState.copy(
                items = emptyList(),
                selectedCategories = emptySet()
            ),
            onBackClick = {},
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TimelineContentEmptyPreview(){
    GroupProjectAppTheme{
        TimelineContent(
            uiState = TimelineUiState(
                // test the empty list state (no tasks at all), not loading state
                isLoading = false,
                items = emptyList(),
                totalTaskCount = 0
            ),
            onBackClick = {},
            onOpenDocs = {},
            onFilterToggle = {}
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
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}

// 2x font: minimum day width (56.sp) doubles, so columns don't squash and the chart scrolls instead.
// Legend chips should wrap onto a second line rather than squash.
@Preview(showBackground = true, name = "Normal Font")
@Preview(showBackground = true, fontScale = 2f, name = "2x Font Scale")
@Composable
private fun TimelineContentSkeletonPreview() {
    GroupProjectAppTheme {
        TimelineContent(
            uiState = sampleTimelineUiState,
            onBackClick = {},
            onOpenDocs = {},
            onFilterToggle = {}
        )
    }
}