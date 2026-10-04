package com.example.groupprojectapp.timeline.ui

import java.time.LocalDate

/**
 * UI STATE (MVVM):
 * everything the Timeline screen displays, in one immutable data class.
 *
 * Produced by [TimelineViewModel] and exposed as a StateFlow; the VIEW only
 * reads it. All fields are vals, so a change means a new TimelineUiState
 * is emitted rather than an old one being edited.
 *
 * isLoading defaults to true so the spinner shows until the first list of
 * tasks arrives from the database.
 */
data class TimelineUiState(
    val items: List<TimelineItem> = emptyList(),
    val isLoading: Boolean = true, // displays a loading spinner or otherwise render the list when false
    val firstDate: LocalDate = LocalDate.now(),
    val totalNumDays: Int = 14, // total number of days the chart covers
    val todayOffset: Int = 0, // how many days from the first date to today (for the today line)
)

