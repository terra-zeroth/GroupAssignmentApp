package com.example.groupprojectapp.home

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.groupprojectapp.tasks.data.TaskStatus
import com.example.groupprojectapp.timeline.ui.components.timelineItemColor
import com.example.groupprojectapp.ui.theme.GroupProjectAppTheme

/**
 * UI MODEL: one row in the "Your current to-do" card.
 */
data class HomeTaskItem(
    val title: String,
    val status: TaskStatus,
    val isOverdue: Boolean
)

/** One tile in the "Go to" grid: its label, the screen name to navigate to, and its icon. */
private data class HomeDestination(
    val label: String,
    val screen: String,
    val icon: ImageVector
)

// Screen names must match the ones MainActivity switches on
private val homeDestinations = listOf(
    HomeDestination("Tasks", "Tasks", Icons.Filled.CheckCircle),
    HomeDestination("Timeline", "Timeline", Icons.Filled.DateRange),
    HomeDestination("Documentation", "Documentation", Icons.Filled.Edit),
    HomeDestination("GitHub", "Github", Icons.Filled.Code),
    HomeDestination("Settings", "Settings", Icons.Filled.Settings)
)

/**
 * VIEW (MVVM):
 * the Home dashboard.
 *
 * Shows: a top bar with the group name and a greeting, a progress card
 * (completion ring), a "Your current to-do" card with status colour strips,
 * a 2-column grid of tiles for each section, and a Log out button.
 *
 * Does NOT: read the database or calculate anything. HomeScreen does that
 * and passes plain values in, so this composable can be previewed.
 *
 * Colours (matching Tasks, Timeline and Documentation): periwinkle top bar
 * with a navy line underneath; cards and tiles with a navy border; to-do
 * strips use the same status colours as the timeline bars.
 *
 * Accessibility (R5): each to-do row shows its status as text as well as
 * colour; tiles are buttons labelled by their text; the grid and cards
 * scroll so large text isn't cut off.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    groupName: String,
    userName: String,
    completionPercent: Int,
    currentTasks: List<HomeTaskItem>,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            // Top bar with a navy line underneath, matching the other screens
            Column {
                TopAppBar(
                    title = {
                        Column {
                            Text(
                                text = groupName.ifBlank { "Dashboard" },
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "Hi, $userName",
                                style = MaterialTheme.typography.bodySmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                )
                HorizontalDivider(
                    thickness = 2.dp,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Progress + to-do side by side, same height
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompletionCard(
                    percent = completionPercent,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
                CurrentTodoCard(
                    tasks = currentTasks,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                )
            }

            Text(
                text = "Go to",
                style = MaterialTheme.typography.titleMedium
            )

            // 2-column grid of section tiles (Rows, not a lazy grid, because the page already scrolls)
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                homeDestinations.chunked(2).forEach { rowItems ->
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        rowItems.forEach { destination ->
                            HomeNavTile(
                                destination = destination,
                                onClick = { onNavigate(destination.screen) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        // Keep the last tile half-width when there's an odd number
                        if (rowItems.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Log out: clears the session and returns to Login
            // Same style as the + buttons: periwinkle fill, navy border and text, rounded-square corners.
            OutlinedButton(
                onClick = onLogout,
                shape = FloatingActionButtonDefaults.shape,
                border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                    contentDescription = null // the button's text already says "Log out"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log out")
            }
        }
    }
}

/** Card with a ring showing what share of the user's tasks are done. */
@Composable
private fun CompletionCard(
    percent: Int,
    modifier: Modifier = Modifier
) {
    HomeCard(modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Your progress",
                style = MaterialTheme.typography.titleSmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(88.dp)
            ) {
                CircularProgressIndicator(
                    progress = { percent.coerceIn(0, 100) / 100f },
                    strokeWidth = 8.dp,
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = "$percent%",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "of your tasks done",
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Card listing the user's current tasks, each with a status colour strip and status text. */
@Composable
private fun CurrentTodoCard(
    tasks: List<HomeTaskItem>,
    modifier: Modifier = Modifier
) {
    HomeCard(modifier = modifier) {
        Column(
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = "Your current to-do",
                style = MaterialTheme.typography.titleSmall
            )
            if (tasks.isEmpty()) {
                Text(
                    text = "Nothing assigned to you.",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                tasks.forEach { task -> TodoRow(task) }
            }
        }
    }
}

/** One to-do row: coloured strip on the left (same colours as the timeline), title and status text. */
@Composable
private fun TodoRow(task: HomeTaskItem) {
    val shape = RoundedCornerShape(4.dp)
    val stripColor = timelineItemColor(isOverdue = task.isOverdue, status = task.status)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(shape)
            .border(1.dp, MaterialTheme.colorScheme.outline, shape)
    ) {
        Box(
            modifier = Modifier
                .width(6.dp)
                .fillMaxHeight()
                .background(stripColor)
        )
        Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = homeStatusText(task),
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1
            )
        }
    }
}

/** One section tile: icon in a periwinkle circle, label underneath. The whole tile is the button. */
@Composable
private fun HomeNavTile(
    destination: HomeDestination,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    HomeCard(onClick = onClick, modifier = modifier) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.tertiaryContainer)
                    .border(1.5.dp, MaterialTheme.colorScheme.outline, CircleShape)
            ) {
                Icon(
                    imageVector = destination.icon,
                    contentDescription = null, // the label below names the tile
                    tint = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = destination.label,
                style = MaterialTheme.typography.titleSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

/** Shared card style for Home: off-white (dark blue in dark mode) with a navy border. */
@Composable
private fun HomeCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val colors = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        contentColor = MaterialTheme.colorScheme.onSurface
    )
    val border = BorderStroke(1.5.dp, MaterialTheme.colorScheme.outline)

    if (onClick != null) {
        Card(onClick = onClick, colors = colors, border = border, modifier = modifier) { content() }
    } else {
        Card(colors = colors, border = border, modifier = modifier) { content() }
    }
}

/** Status as text, overdue first, so colour isn't the only signal (R5). */
private fun homeStatusText(task: HomeTaskItem): String = when {
    task.isOverdue -> "Overdue"
    else -> when (task.status) {
        TaskStatus.TODO -> "To do"
        TaskStatus.IN_PROGRESS -> "In progress"
        TaskStatus.DONE -> "Done"
    }
}

// SHARED SAMPLE DATA FOR PREVIEWS
private val sampleHomeTasks = listOf(
    HomeTaskItem(title = "Login screen", status = TaskStatus.TODO, isOverdue = true),
    HomeTaskItem(title = "Timeline UI", status = TaskStatus.IN_PROGRESS, isOverdue = false),
    HomeTaskItem(title = "README", status = TaskStatus.TODO, isOverdue = false)
)

// PREVIEWS ------------

@Preview(showBackground = true, name = "Light Mode")
@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Dark Mode")
@Composable
private fun HomeContentPreview() {
    GroupProjectAppTheme {
        HomeContent(
            groupName = "Team Kotlin",
            userName = "Aliyah",
            completionPercent = 62,
            currentTasks = sampleHomeTasks,
            onNavigate = {},
            onLogout = {}
        )
    }
}

// Nothing assigned yet, 0% done
@Preview(showBackground = true, name = "Empty To-Do")
@Composable
private fun HomeContentEmptyPreview() {
    GroupProjectAppTheme {
        HomeContent(
            groupName = "Team Kotlin",
            userName = "Aliyah",
            completionPercent = 0,
            currentTasks = emptyList(),
            onNavigate = {},
            onLogout = {}
        )
    }
}

// 2x font: the page should scroll rather than cut anything off
@Preview(showBackground = true, fontScale = 2f, name = "2x Font Scale")
@Composable
private fun HomeContentLargeFontPreview() {
    GroupProjectAppTheme {
        HomeContent(
            groupName = "Team Kotlin",
            userName = "Aliyah",
            completionPercent = 62,
            currentTasks = sampleHomeTasks,
            onNavigate = {},
            onLogout = {}
        )
    }
}