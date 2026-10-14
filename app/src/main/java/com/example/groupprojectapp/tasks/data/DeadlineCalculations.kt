package com.example.groupprojectapp.tasks.data

import java.time.LocalDate

/**
 * Shared deadline calculations (M5) — used by the Tasks list and available
 * for Aliyah's Timeline to reuse, so the two screens never disagree about
 * what "3 days left" or "overdue" means. Pure functions: given a date and
 * "today", they return a value or a label, with no side effects and no
 * dependency on Room, Compose or any ViewModel — that's what keeps this a
 * calculation (R2) rather than UI logic.
 */
object DeadlineCalculations {

    /** Positive = days remaining, 0 = due today, negative = days overdue. */
    fun daysUntilDue(dueDateEpochDay: Long, today: LocalDate = LocalDate.now()): Long =
        dueDateEpochDay - today.toEpochDay()

    /** Human-readable label, e.g. "3 days left", "Due today", "Overdue by 2 days". */
    fun deadlineLabel(dueDateEpochDay: Long, today: LocalDate = LocalDate.now()): String {
        val days = daysUntilDue(dueDateEpochDay, today)
        return when {
            days > 0 -> "$days day${if (days == 1L) "" else "s"} left"
            days == 0L -> "Due today"
            else -> "Overdue by ${-days} day${if (days == -1L) "" else "s"}"
        }
    }
}
