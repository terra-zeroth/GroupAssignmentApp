package com.example.groupprojectapp.documentation.ui

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
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme
import java.time.LocalDate



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentationContent(
    uiState: DocumentationUiState,
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
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
                            text = "No documentation entries found.\nTap + to add an entry.",
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
                        items(
                            items = uiState.entries,
                            key = { entry -> entry.id }
                        ){ item ->
                            DocumentationEntryCard(item = item)
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
    modifier: Modifier = Modifier
){
    Card(
        modifier = modifier.fillMaxWidth()
    ){
        Column(modifier = Modifier.padding(16.dp)){

            // Task Title
            Text(
                text = item.taskTitle,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Date
            Text(
                text = item.date.toString(),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Extra notes
            Text(
                text = item.note,
                style = MaterialTheme.typography.bodyMedium
            )


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
        imageUri = null
    ),

    DocumentationEntryItem(
        id = 2L,
        taskTitle = "UI Component Review",
        date = LocalDate.of(2026, 10, 3),
        note = "Review timeline components with the design team",
        imageUri = null
    ),

    DocumentationEntryItem(
        id = 3L,
        taskTitle = "Review overall system",
        date = LocalDate.of(2026, 10, 8),
        note = "Double check everything meets the requirements and works without errors",
        imageUri = null
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
            onAddClick = {}
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
            onAddClick = {}
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
            onAddClick = {}
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
            onAddClick = {}
        )
    }
}