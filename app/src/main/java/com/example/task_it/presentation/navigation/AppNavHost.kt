package com.example.task_it.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task_it.presentation.tasks.form.TaskFormScreen
import com.example.task_it.presentation.tasks.list.TaskListScreen

@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.TASK_LIST
    ) {

        composable(NavRoutes.TASK_LIST) {
            TaskListScreen(
                onAddTaskClick = { navController.navigate(NavRoutes.taskFormRoute()) },
                onEditTaskClick = { taskId ->
                    navController.navigate(NavRoutes.taskFormRoute(taskId))
                },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }

        composable(
            route = NavRoutes.TASK_FORM_WITH_ARGS,
            arguments = listOf(
                navArgument("taskId") {
                    type = NavType.LongType
                    defaultValue = -1L
                }
            )
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getLong("taskId") ?: -1L
            val effectiveTaskId = taskId.takeIf { it != -1L }

            TaskFormScreen(
                taskId = effectiveTaskId, // ✅ null = crear, id = editar
                onCancel = { navController.popBackStack() },
                onCreateTask = { navController.popBackStack() }
            )
        }
    }
}
