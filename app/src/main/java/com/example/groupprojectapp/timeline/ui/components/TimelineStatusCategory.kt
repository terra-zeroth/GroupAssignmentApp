package com.example.groupprojectapp.timeline.ui.components

/**
 * The four status categories the Timeline can filter by.
 *
 * Overdue isn't a real TaskStatus (it's worked out from the due date),
 * so the timeline needs its own list that includes it.
 *
 * label is the text shown on each legend/filter chip and matches
 * timelineStatusText. Order here = order in the legend.
 */
enum class TimelineStatusCategory(val label: String) {
    DONE("Done"),
    IN_PROGRESS("In Progress"),
    TODO("To Do"),
    OVERDUE("Overdue")
}