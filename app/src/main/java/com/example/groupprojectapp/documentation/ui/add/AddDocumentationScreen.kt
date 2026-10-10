package com.example.groupprojectapp.documentation.ui.add

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun AddDocumentationScreen(
    onBackClick: () -> Unit,
    onSaved: () -> Unit,
    modifier: Modifier = Modifier
) {
    AddDocumentationFeature(
        onBackClick = onBackClick,
        onSaved = onSaved,
        modifier = modifier
    )
}