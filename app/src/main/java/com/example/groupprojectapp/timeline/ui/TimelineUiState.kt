package com.example.groupprojectapp.timeline.ui

import java.time.LocalDate

data class TimelineUiState(
    val items: List<TimelineItem> = emptyList(),
    val isLoading: Boolean = true, // displays a loading spinner or otherwise render the list when false
    val firstDate: LocalDate = LocalDate.now(),
    val totalNumDays: Int = 14, // total number of days the chart covers
    // TODO: possibly based on task's due date max + 1
    val todayOffset: Int = 0, // how many days from the first date to today (for the today line)
)

