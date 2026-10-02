package com.example.groupprojectapp.tasks.ui

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.groupprojectapp.tasks.TaskContainer

/**
 * Route names/args for the Tasks feature. Kept here (not in MainActivity)
 * so this feature owns its own navigation contract end to end.
 */
object TaskRoutes {
    const val LIST = "tasks_list"
    const val DETAIL_ARG = "taskId"
    const val DETAIL = "task_detail"
    const val DETAIL_ROUTE = "$DETAIL/{$DETAIL_ARG}"

    /** Sentinel meaning "no id yet" — i.e. this is a brand new task. */
    const val NEW_TASK_ID = -1L

    fun detailRoute(taskId: Long?) = "$DETAIL/${taskId ?: NEW_TASK_ID}"
}

/**
 *  * VIEW layer entry point: creates the container and factory once, then
 *  * switches between the list screen and the detail screen.
 *
 * Self-contained navigation for the Tasks feature (list + detail-by-id).
 * This is deliberately its OWN NavHost rather than a change to
 * MainActivity's outer screen switch: it's the only thing that has to be
 * "real" Navigation-Compose navigation for R1 (a detail screen reached by
 * passing an ID, with working back navigation), and keeping it scoped here
 * means it doesn't touch or conflict with anyone else's screens.
 *
 * MainActivity keeps calling `TasksScreen(userName = userName)` exactly as
 * it already does — see tasks.kt, which now just delegates here.
 */
@Composable
fun TasksFeature(currentUserName: String) {
    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) }
    val factory = remember(container) { taskViewModelFactory(container) }

    // Let the system/gesture back button pop OUR stack (detail -> list)
    // instead of falling through to whatever the outer app would otherwise
    // do, since MainActivity has no NavController of its own to catch it.
    val backStackEntry by navController.currentBackStackEntryAsState()
    BackHandler(enabled = backStackEntry?.destination?.route != TaskRoutes.LIST) {
        navController.popBackStack()
    }

    NavHost(navController = navController, startDestination = TaskRoutes.LIST) {
        composable(TaskRoutes.LIST) {
            val viewModel: TaskListViewModel = viewModel(factory = factory)
            TaskListScreen(
                viewModel = viewModel,
                currentUserName = currentUserName,
                onTaskClick = { taskId -> navController.navigate(TaskRoutes.detailRoute(taskId)) },
                onAddTaskClick = { navController.navigate(TaskRoutes.detailRoute(null)) }
            )
        }
        composable(
            route = TaskRoutes.DETAIL_ROUTE,
            arguments = listOf(navArgument(TaskRoutes.DETAIL_ARG) { type = NavType.LongType })
        ) {
            // This is the "detail screen reached by passing an ID through
            // the navigation flow" R1 asks for: the taskId argument above
            // flows into TaskDetailViewModel via SavedStateHandle.
            val viewModel: TaskDetailViewModel = viewModel(factory = factory)
            TaskDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
