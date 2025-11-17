package com.example.task_it.presentation.tasks.form

import java.time.LocalDate
import java.time.LocalTime

data class TaskFormUiState(
    val title: String = "",
    val priority: TaskPriority = TaskPriority.MEDIA,
    val date: LocalDate? = null,
    val time: LocalTime? = null,
    val location: String = ""
)

enum class TaskPriority {
    CRITICA, ALTA, MEDIA, BAJA
}
