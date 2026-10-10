package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groupprojectapp.documentation.ui.DocumentationFeature

@Composable
fun DocumentationScreen(
    onBackClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    DocumentationFeature(
        onBackClick = onBackClick,
        onAddClick = onAddClick,
        modifier = modifier
    )
}