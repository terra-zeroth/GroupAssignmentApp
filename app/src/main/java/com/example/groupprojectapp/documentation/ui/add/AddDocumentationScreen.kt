package com.example.groupprojectapp.documentation.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Thin delegate on purpose, matching timeline.kt: MainActivity calls
 * AddDocumentationScreen(...) and doesn't need to know about the
 * ViewModel, container or picker inside the documentation package.
 * See AddDocumentationFeature for the real implementation.
 *
 * entryId: null = add a new entry; an id = edit that entry.
 */

@Composable
fun AddDocumentationScreen(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    currentUsername: String,
    entryId: Long?,
    modifier: Modifier = Modifier
) {
    AddDocumentationFeature(
        onBackClick = onBackClick,
        onSaved = onSaved,
        currentUsername = currentUsername,
        entryId = entryId,
        modifier = modifier
    )
}