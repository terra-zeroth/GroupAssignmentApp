package com.example.groupprojectapp.timeline.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.data.TimelineItem
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

// TODO:
/*
    date header
    one row per task with
       title
       assignee
       bar positioned
       sized
       use startOffsetDays/lengthDays
       colour by status
    show status as text/icon too, not just colour for (R5) Accessibility
    today line
    empty state when there are no tasks
    horizontal scrolling
 */

/*
   Possible things to take note of.
   if the text increase for accessibility you'd have to
      - stop the text from wrapping into one line somehow
      - let the row height grow with the user's text size
      - so use sp instead of dp for the row height
*/

@Composable
fun TimelineContent(
    uiState: TimelineUiState,
    modifier: Modifier = Modifier
){
    // no scroll state parameter
    // cause Horizontal scroll state can be managed within TimelineContent using
    // TODO: add rememberScrollState() so ViewModel nor Previews need to observe or control scroll position

    // PLACEHOLDER so preview can render for now
    Text(
        text = "Number of items: ${uiState.items.size}",
        modifier = modifier
    )

}

@Preview(showBackground = true)
@Composable
private fun TimelineContentPreview(){
    GroupProjectAppTheme {
        TimelineContent(
            uiState = TimelineUiState(
                isLoading = false,
                todayOffset = 3,
                firstDate = LocalDate.of(2026, 10, 1),
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
                        startOffsetDays = 4,
                        lengthDays = 2,
                        status = TaskStatus.TODO,
                        assigneeName = "Manasviba",
                        isOverdue = true
                    )
                )
            )
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





