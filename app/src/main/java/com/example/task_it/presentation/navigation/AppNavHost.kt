package com.example.task_it.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
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
                onAddTaskClick = { navController.navigate(NavRoutes.TASK_FORM) },
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        }


        composable(NavRoutes.TASK_FORM) {
            TaskFormScreen(
                onCancel = { navController.popBackStack() },
                onCreateTask = {
                    // Más adelante guardaremos la tarea
                    navController.popBackStack()
                }
            )
        }
    }
}
