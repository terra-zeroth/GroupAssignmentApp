package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groupprojectapp.timeline.ui.TimelineFeature

/**
 * Kept as a thin delegate on purpose, matching tasks.kt: MainActivity calls
 * TimelineScreen(onBackClick = ...) and doesn't need to know anything about
 * the timeline/ package (ViewModel, container, components).
 * See com.example.groupprojectapp.timeline.ui.TimelineFeature for the real
 * implementation.
 */
@Composable
fun TimelineScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TimelineFeature(onBackClick = onBackClick, modifier = modifier)
}
