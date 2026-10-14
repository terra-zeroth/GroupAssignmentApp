package com.example.groupprojectapp.documentation.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import com.example.groupprojectapp.ui.theme.Navy
import com.example.groupprojectapp.ui.theme.StatusDone
import java.time.LocalDate

/**
 * VIEW (MVVM):
 * the Documentation log screen (A3).
 *
 * Shows:
 * a top bar with Back (titled with the task's name when filtered,
 * A5) and a status filter menu, a row of author chips, a + button to add
 * an entry, then one of four states: loading spinner, "no entries match
 * these filters", empty message, or a LazyColumn of entry cards (task
 * title, date, author, note, and screenshot if attached). Tapping a card
 * opens it for editing.
 *
 * Filters: status is a top-bar menu (same pattern as the Tasks sort menu);
 * author is a scrolling row of chips (same pattern as the Tasks assignee
 * chips). When a filter is on, a "Showing: … · Clear" line says so.
 *
 * Does NOT:
 * query, filter or convert anything. It draws [DocumentationUiState] and
 * reports clicks through its lambdas, so the previews can supply fake state.
 *
 * LazyColumn because the number of entries keeps growing;
 * it only composes the cards on screen.
 *
 * Colours (matching the Timeline): the top bar uses tertiaryContainer
 * (periwinkle / dark blue) with a navy line underneath; the + button is
 * periwinkle with a navy border; entry cards are green when the task is
 * completed, red when overdue, otherwise off-white / dark blue, all with a
 * navy border.
 *
 * Accessibility (R5):
 * icon-only buttons have content descriptions; the selected status has a
 * "Selected" check; cards show a written "Completed" / "Overdue" label as
 * well as colour; the author chips scroll sideways so large text isn't cut
 * off; text uses Material type styles so it scales with the user's font size.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationContent(
    uiState: DocumentationUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEntryClick: (Long) -> Unit,
    onStatusFilterSelected: (StatusFilter) -> Unit,
    onAuthorFilterSelected: (String?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
){
    // UI-only state: whether the status menu is open
    var statusMenuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            // Top bar with a navy line underneath, matching the Timeline
            Column {
                TopAppBar(
                    title = {
                        // Uses the filter title if present, otherwise defaults to "Documentation"
                        Text(uiState.filterTaskTitle ?: "Documentation")
                    },
                    // Top bar uses tertiaryContainer from the theme (periwinkle / dark blue)
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        navigationIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        actionIconContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    ),
                    navigationIcon = {
                        IconButton(onClick = onBackClick) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back to Dashboard"
                            )
                        }
                    },
                    actions = {
                        // Status filter menu (same pattern as the Tasks sort menu)
                        Box {
                            IconButton(onClick = { statusMenuExpanded = true }) {
                                Icon(
                                    imageVector = Icons.Filled.FilterList,
                                    contentDescription = "Filter by status"
                                )
                            }
                            DropdownMenu(
                                expanded = statusMenuExpanded,
                                onDismissRequest = { statusMenuExpanded = false }
                            ) {
                                StatusFilter.entries.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.label()) },
                                        onClick = {
                                            onStatusFilterSelected(status)
                                            statusMenuExpanded = false
                                        },
                                        // Check mark on the current choice, so the user can see what's on
                                        trailingIcon = {
                                            if (uiState.filters.status == status) {
                                                Icon(
                                                    imageVector = Icons.Filled.Check,
                                                    contentDescription = "Selected"
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                )

                // Navy border line along the bottom of the top bar
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },

        floatingActionButton = {
            // Periwinkle fill, navy icon and navy border, matching the Timeline's Today button
            FloatingActionButton(
                onClick = onAddClick,
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.onTertiaryContainer,
                modifier = Modifier.border(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = FloatingActionButtonDefaults.shape
                )
            ){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add documentation entry"
                )
            }
        },

        modifier = modifier
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Author chips (hidden while loading or when there's nobody to filter by)
            if (!uiState.isLoading && uiState.authorOptions.isNotEmpty()) {
                AuthorFilterRow(
                    authorOptions = uiState.authorOptions,
                    selectedAuthor = uiState.filters.selectedAuthor,
                    onAuthorFilterSelected = onAuthorFilterSelected
                )
            }

            // "Showing: … · Clear" so the user always knows a filter is on
            if (uiState.filters.isActive) {
                ActiveFiltersLine(
                    filters = uiState.filters,
                    onClearFilters = onClearFilters
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ){
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }

                    // Entries exist, but the filters hide all of them
                    uiState.entries.isEmpty() && uiState.filters.isActive -> {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "No entries match these filters.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            TextButton(onClick = onClearFilters) {
                                Text("Clear filters")
                            }
                        }
                    }

                    uiState.entries.isEmpty() -> {
                        // Empty state message for when there are no items
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (uiState.filterTaskTitle != null)
                                    "No entries for this task yet.\nTap + to add one."
                                else
                                    "No documentation entries found.\nTap + to add an entry.",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    else -> {

                        // List of Documentation items
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.fillMaxSize()
                        ){

                            item {
                                Text(
                                    text = "Tap an entry to edit it.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            // key = entry id, so Compose keeps each card's state matched to the right
                            // entry when the list changes
                            items(
                                items = uiState.entries,
                                key = { entry -> entry.id }
                            ){ item ->
                                DocumentationEntryCard(
                                    item = item,
                                    onEntryClick = onEntryClick
                                )
                            }
                        }

                    }

                }
            }
        }
    }
}

/**
 * Author chips: "All" first, then one per person (same pattern as the Tasks
 * assignee chips). LazyRow scrolls sideways, so names aren't cut off at 2x
 * text (R5). Styled like the Timeline legend: navy border, white text when
 * selected.
 */
@Composable
private fun AuthorFilterRow(
    authorOptions: List<String>,
    selectedAuthor: String?,
    onAuthorFilterSelected: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        // "All" is a single fixed chip, so item { }
        item {
            DocumentationFilterChip(
                label = "All",
                selected = selectedAuthor == null,
                onClick = { onAuthorFilterSelected(null) }
            )
        }
        // One chip per name, so items(...); the name itself is the key
        items(authorOptions, key = { it }) { name ->
            DocumentationFilterChip(
                label = name,
                selected = selectedAuthor == name,
                onClick = { onAuthorFilterSelected(name) }
            )
        }
    }
}

/** One filter chip in the shared style: navy border, white text when selected. */
@Composable
private fun DocumentationFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label, maxLines = 1) },
        colors = FilterChipDefaults.filterChipColors(
            selectedLabelColor = Color.White
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            borderColor = MaterialTheme.colorScheme.outline,
            selectedBorderColor = MaterialTheme.colorScheme.outline,
            borderWidth = 1.5.dp,
            selectedBorderWidth = 1.5.dp
        ),
        modifier = modifier
    )
}

/** Says which filters are on (e.g. "Showing: Overdue · Lili"), with a Clear button. */
@Composable
private fun ActiveFiltersLine(
    filters: DocumentationFilters,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    val parts = buildList {
        if (filters.status != StatusFilter.ALL) add(filters.status.label())
        filters.selectedAuthor?.let { add(it) }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 8.dp)
    ) {
        Text(
            text = "Showing: ${parts.joinToString(" · ")}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )
        TextButton(onClick = onClearFilters) {
            Text("Clear")
        }
    }
}

@Composable
private fun DocumentationEntryCard(
    item: DocumentationEntryItem,
    onEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    // Card colours by status. Overdue is checked first, like the Timeline.
    // Overdue = red with white text (navy on red is too low contrast)
    // Completed = green with navy text
    // Otherwise = the normal off-white / dark blue card
    val cardColor = when {
        item.isOverdue -> MaterialTheme.colorScheme.error
        item.status == TaskStatus.DONE -> StatusDone
        else -> MaterialTheme.colorScheme.surfaceContainerLow
    }
    val textColor = when {
        item.isOverdue -> MaterialTheme.colorScheme.onError
        item.status == TaskStatus.DONE -> Navy
        else -> MaterialTheme.colorScheme.onSurface
    }

    // Written status so colour isn't the only signal (R5)
    val statusLabel = when {
        item.isOverdue -> "Overdue"
        item.status == TaskStatus.DONE -> "Completed"
        else -> null
    }

    Card(
        onClick = { onEntryClick(item.id)},
        colors = CardDefaults.cardColors(
            containerColor = cardColor,
            contentColor = textColor
        ),
        border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
        modifier = modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(16.dp)){

            // Status label (only for Completed / Overdue)
            statusLabel?.let { label ->
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            // Task Title
            Text(
                text = item.taskTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Extra Submission details (date and author).
            // No fixed colour, so they use the card's text colour (white on red, navy on green).
            Text(
                text = item.date.toString(),
                style = MaterialTheme.typography.labelSmall
            )

            // Author name near the date
            Text(
                text = "Added by ${item.authorName}",
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Extra notes
            Text(
                text = item.note,
                style = MaterialTheme.typography.bodyMedium
            )

            // Only show the image if one was attached
            item.imageUri?.let { uri ->
                Spacer(modifier = Modifier.height(8.dp))
                AsyncImage(
                    model = uri,
                    contentDescription = "Screenshot for this entry",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                )
            }
        }
    }
}

// SHARED SAMPLE DATA FOR PREVIEWS
// One completed (green), one overdue (red), one to do (normal card)

val sampleDocumentationItems = listOf(
    DocumentationEntryItem(
        id = 1L,
        taskTitle = "Design Database Schema",
        date = LocalDate.of(2026, 10, 1),
        note = "Finished drafting the Room entities and initial ER diagram",
        imageUri = null,
        authorName = "Manasviba",
        status = TaskStatus.DONE,
        isOverdue = false
    ),

    DocumentationEntryItem(
        id = 2L,
        taskTitle = "UI Component Review",
        date = LocalDate.of(2026, 10, 3),
        note = "Review timeline components with the design team",
        imageUri = null,
        authorName = "Aliyah",
        status = TaskStatus.IN_PROGRESS,
        isOverdue = true
    ),

    DocumentationEntryItem(
        id = 3L,
        taskTitle = "Review overall system",
        date = LocalDate.of(2026, 10, 8),
        note = "Double check everything meets the requirements and works without errors",
        imageUri = null,
        authorName = "Twisha",
        status = TaskStatus.TODO,
        isOverdue = false
    )

)

private val sampleAuthorOptions = listOf("Aliyah", "Manasviba", "Twisha")


// PREVIEWS ------------
// Note: filter taps do nothing in Previews (lambdas are {}); test filtering on the emulator.

// 1. Standard Content Preview (Populated List)
@Preview(showBackground = true, name = "Content Loaded")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Content Loaded - Dark")
@Preview(fontScale = 2.0f, showBackground = true, name = "Content Loaded - Large Font")
@Composable
private fun PreviewDocumentationContentList() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = sampleDocumentationItems,
                authorOptions = sampleAuthorOptions
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 2. Filtered Content Preview (Task Filter Active)
@Preview(showBackground = true, name = "Filtered by Task")
@Composable
private fun PreviewDocumentationContentFiltered() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = listOf(sampleDocumentationItems.first()),
                filterTaskTitle = "Design Database Schema",
                authorOptions = sampleAuthorOptions
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 3. Status + author filters active, with a result ("Showing: Overdue · Aliyah")
@Preview(showBackground = true, name = "Filters Active")
@Preview(fontScale = 2.0f, showBackground = true, name = "Filters Active - Large Font")
@Composable
private fun PreviewDocumentationContentFiltersActive() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = listOf(sampleDocumentationItems[1]),
                authorOptions = sampleAuthorOptions,
                filters = DocumentationFilters(
                    status = StatusFilter.OVERDUE,
                    selectedAuthor = "Aliyah"
                )
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 4. Filters hide everything: "No entries match these filters" + Clear
@Preview(showBackground = true, name = "Filters - No Matches")
@Composable
private fun PreviewDocumentationContentNoMatches() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = emptyList(),
                authorOptions = sampleAuthorOptions,
                filters = DocumentationFilters(status = StatusFilter.DONE, selectedAuthor = "Twisha")
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 5. Loading State Preview
@Preview(showBackground = true, name = "Loading State")
@Composable
private fun PreviewDocumentationContentLoading() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = true,
                entries = emptyList()
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 6. Empty State Preview
@Preview(showBackground = true, name = "Empty State")
@Composable
private fun PreviewDocumentationContentEmpty() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = emptyList()
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}

// 7. Empty filtered state preview for when pressed on timeline
@Preview(showBackground = true, name = "Filtered - Empty")
@Composable
private fun PreviewDocumentationContentFilteredEmpty() {
    GroupProjectAppTheme {
        DocumentationContent(
            uiState = DocumentationUiState(
                isLoading = false,
                entries = emptyList(),
                filterTaskTitle = "Design Database Schema"
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {},
            onStatusFilterSelected = {},
            onAuthorFilterSelected = {},
            onClearFilters = {}
        )
    }
}