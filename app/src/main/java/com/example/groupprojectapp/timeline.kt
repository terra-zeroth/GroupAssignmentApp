package com.example.groupprojectapp

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.groupprojectapp.timeline.ui.TimelineFeature

@Composable
fun TimelineScreen(modifier: Modifier = Modifier) {
    TimelineFeature(modifier = modifier)
}