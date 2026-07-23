package com.example.task_it.presentation.navigation

object NavRoutes {
    const val MAIN = "main"
    const val TASK_FORM = "task_form"
    const val TASK_FORM_WITH_ARGS = "task_form?taskId={taskId}"

    fun taskFormRoute(taskId: Long? = null): String =
        if (taskId == null) TASK_FORM else "task_form?taskId=$taskId"
}