package com.example.task_it.presentation.tasks.form

import com.example.task_it.domain.model.TaskPriority
import java.time.LocalDate
import java.time.LocalTime

data class TaskFormUiState(
    val title: String = "",
    val description: String = "",
    val priority: TaskPriority = TaskPriority.MEDIA,
    val date: LocalDate = LocalDate.now(),
    val time: LocalTime? = null,
    val location: String? = "",
    val dateTimeError: String? = null,
    val isSubmitEnabled: Boolean = false
)
