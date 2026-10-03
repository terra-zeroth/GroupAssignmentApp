package com.example.groupprojectapp.timeline.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun TimelineLegend(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Done", style = MaterialTheme.typography.labelSmall)
        Text("In Progress", style = MaterialTheme.typography.labelSmall)
        Text("To Do", style = MaterialTheme.typography.labelSmall)
        Text("Overdue", style = MaterialTheme.typography.labelSmall)
    }
}