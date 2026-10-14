package com.example.groupprojectapp.documentation.ui.list

/**
 * Status choices for the documentation filter.
 *
 * OVERDUE isn't a TaskStatus (it's worked out from the due date), which is
 * why this is the screen's own enum. The options don't overlap: To do and
 * In progress leave out overdue tasks, matching the Timeline legend.
 *
 * label() is the text shown in the menu, like TaskSortOrder.
 */
enum class StatusFilter {
    ALL, TODO, IN_PROGRESS, DONE, OVERDUE;

    fun label(): String = when (this) {
        ALL -> "All"
        TODO -> "To do"
        IN_PROGRESS -> "In progress"
        DONE -> "Completed"
        OVERDUE -> "Overdue"
    }
}

/**
 * UI STATE (MVVM):
 * the documentation list's current filter choices.
 *
 * ALL / null means "don't filter by this". Held in DocumentationViewModel,
 * so the choices survive rotation.
 *
 * isActive is true when any filter is on; the screen uses it to show the
 * "Showing: … · Clear" line and the "No entries match these filters" message.
 */
data class DocumentationFilters(
    val status: StatusFilter = StatusFilter.ALL,
    val selectedAuthor: String? = null
) {
    val isActive: Boolean
        get() = status != StatusFilter.ALL || selectedAuthor != null
}