package com.example.task_it.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.task_it.presentation.MainPagerScreen
import com.example.task_it.presentation.tasks.form.TaskFormScreen

@Composable
fun AppNavHost(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = NavRoutes.MAIN
    ) {

        composable(NavRoutes.MAIN) {
            MainPagerScreen(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onAddTaskClick = { navController.navigate(NavRoutes.taskFormRoute()) },
                onEditTaskClick = { taskId ->
                    navController.navigate(NavRoutes.taskFormRoute(taskId))
                }
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
                taskId = effectiveTaskId,
                onCancel = { navController.popBackStack() },
                onCreateTask = { navController.popBackStack() }
            )
        }
    }
}