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
    const val ASSIGNEE_ARG = "assigneeId"
    const val DETAIL = "task_detail"
    const val DETAIL_ROUTE = "$DETAIL/{$DETAIL_ARG}?$ASSIGNEE_ARG={$ASSIGNEE_ARG}"

    /** Sentinel meaning "no id yet" — i.e. this is a brand new task. */
    const val NEW_TASK_ID = -1L

    /** Sentinel meaning "no default assignee was passed in". */
    const val NO_DEFAULT_ASSIGNEE = -1L

    fun detailRoute(taskId: Long?, defaultAssigneeId: Long? = null) =
        "$DETAIL/${taskId ?: NEW_TASK_ID}?$ASSIGNEE_ARG=${defaultAssigneeId ?: NO_DEFAULT_ASSIGNEE}"
}

/**
 * VIEW layer entry point: creates the container and factory once, then
 * switches between the list screen and the detail screen.
 *
 * Self-contained navigation for the Tasks feature (list + detail-by-id).
 * MainActivity keeps calling `TasksScreen(userName = userName)` exactly as
 * it already does — see tasks.kt, which now just delegates here.
 *
 * [onBackToHome] fires when back is pressed while already on the task
 * list (the root of this feature's own stack) — that's the signal to pop
 * up to MainActivity's "home" screen, since there's nothing left in this
 * NavHost's own stack to pop at that point.
 */
@Composable
fun TasksFeature(currentUserName: String, onBackToHome: () -> Unit) {
    val navController = rememberNavController()

    val context = LocalContext.current.applicationContext
    val container = remember { TaskContainer(context) }
    val factory = remember(container) { taskViewModelFactory(container) }

    // Pop OUR stack (detail -> list) first; once we're already at the
    // list, hand control back up to MainActivity via onBackToHome instead
    // of letting the back press fall through to the OS.
    val backStackEntry by navController.currentBackStackEntryAsState()
    BackHandler(enabled = true) {
        if (backStackEntry?.destination?.route != TaskRoutes.LIST) {
            navController.popBackStack()
        } else {
            onBackToHome()
        }
    }

    NavHost(navController = navController, startDestination = TaskRoutes.LIST) {
        composable(TaskRoutes.LIST) {
            val viewModel: TaskListViewModel = viewModel(factory = factory)
            TaskListScreen(
                viewModel = viewModel,
                currentUserName = currentUserName,
                onTaskClick = { taskId -> navController.navigate(TaskRoutes.detailRoute(taskId)) },
                onAddTaskClick = { defaultAssigneeId ->
                    navController.navigate(TaskRoutes.detailRoute(null, defaultAssigneeId))
                },
                onBackToHome = onBackToHome
            )
        }
        composable(
            route = TaskRoutes.DETAIL_ROUTE,
            arguments = listOf(
                navArgument(TaskRoutes.DETAIL_ARG) { type = NavType.LongType },
                navArgument(TaskRoutes.ASSIGNEE_ARG) {
                    type = NavType.LongType
                    defaultValue = TaskRoutes.NO_DEFAULT_ASSIGNEE
                }
            )
        ) {
            val viewModel: TaskDetailViewModel = viewModel(factory = factory)
            TaskDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSaved = { navController.popBackStack() }
            )
        }
    }
}
