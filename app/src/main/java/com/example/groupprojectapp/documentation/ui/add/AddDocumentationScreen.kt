package com.example.groupprojectapp.documentation.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddDocumentationScreen(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    currentUsername: String,
    modifier: Modifier = Modifier
) {
    AddDocumentationFeature(
        onBackClick = onBackClick,
        onSaved = onSaved,
        currentUsername = currentUsername,
        modifier = modifier
    )
}