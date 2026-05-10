package com.example.task_it.presentation.tasks.list

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority

fun filterAndSortTasks(
    tasks: List<Task>,
    selectedPriority: TaskPriority?,
    searchQuery: String
): List<Task> {
    val byPriority =
        if (selectedPriority == null) tasks
        else tasks.filter { it.priority == selectedPriority }

    val query = searchQuery.trim()

    val byTitle =
        if (query.isBlank()) byPriority
        else byPriority.filter { it.title.contains(query, ignoreCase = true) }

    return byTitle.sortedWith(
        compareBy<Task> { it.isCompleted }
            .thenBy { it.date }
    )
}