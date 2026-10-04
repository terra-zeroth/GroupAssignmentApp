package com.example.groupprojectapp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groupprojectapp.timeline.ui.TimelineFeature

@Composable
fun TimelineScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TimelineFeature(onBackClick = onBackClick, modifier = modifier)
}
