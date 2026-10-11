package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groupprojectapp.documentation.ui.list.DocumentationFeature

@Composable
fun DocumentationScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    onEntryClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    DocumentationFeature(
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        onEntryClick = onEntryClick,
        modifier = modifier
    )
}