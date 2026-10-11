package com.example.groupprojectapp.documentation.ui.list

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate

/**
 * VIEW (MVVM):
 * the Documentation log screen (A3).
 *
 * Shows:
 * a top bar with Back (titled with the task's name when filtered,
 * A5), a + button to add an entry, then one of three states: loading
 * spinner, empty message, or a LazyColumn of entry cards (task title,
 * date, author, note, and screenshot if attached). Tapping a card opens
 * it for editing.
 *
 * Does NOT:
 * query, filter or convert anything. It draws
 * [DocumentationUiState] and reports clicks through onBackClick,
 * onAddClick and onEntryClick, so the previews can supply fake state.
 *
 * LazyColumn because the number of entries keeps growing;
 * it only composes the cards on screen.
 *
 * Accessibility (R5):
 * icon-only buttons have content descriptions; text uses Material type styles
 * so it scales with the user's font size.
 */

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationContent(
    uiState: DocumentationUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Uses the filter title if present, otherwise defaults to "Documentation"
                    Text(uiState.filterTaskTitle ?: "Documentation")
                },
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

        floatingActionButton = {
            FloatingActionButton(onClick = onAddClick){
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add documentation entry"
                )
            }
        },

        modifier = modifier
    ) { innerPadding ->


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ){
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
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

@Composable
private fun DocumentationEntryCard(
    item: DocumentationEntryItem,
    onEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
){
    Card(
        onClick = { onEntryClick(item.id)},
        modifier = modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(16.dp)){

            // Task Title
            Text(
                text = item.taskTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Extra Submission details (date and author)
            Text(
                text = item.date.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Author name near the date
            Text(
                text = "Added by ${item.authorName}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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

val sampleDocumentationItems = listOf(
    DocumentationEntryItem(
        id = 1L,
        taskTitle = "Design Database Schema",
        date = LocalDate.of(2026, 10, 1),
        note = "Finished drafiting the Room entities and intial ER diagram",
        imageUri = null,
        authorName = "Manasviba"
    ),

    DocumentationEntryItem(
        id = 2L,
        taskTitle = "UI Component Review",
        date = LocalDate.of(2026, 10, 3),
        note = "Review timeline components with the design team",
        imageUri = null,
        authorName = "Aliyah"
    ),

    DocumentationEntryItem(
        id = 3L,
        taskTitle = "Review overall system",
        date = LocalDate.of(2026, 10, 8),
        note = "Double check everything meets the requirements and works without errors",
        imageUri = null,
        authorName = "Twisha"
    )

)


// PREVIEWS ------------
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
                entries = sampleDocumentationItems
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {}
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
                filterTaskTitle = "Design Database Schema"
            ),
            onBackClick = {},
            onAddClick = {},
            onEntryClick = {}
        )
    }
}

// 3. Loading State Preview
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
            onEntryClick = {}
        )
    }
}

// 4. Empty State Preview
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
            onEntryClick = {}
        )
    }
}

// 5. Empty filtered state preview for when pressed on timeline
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
            onEntryClick = {}
        )
    }
}