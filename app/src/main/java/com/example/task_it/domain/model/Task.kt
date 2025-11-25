package com.example.task_it.domain.model

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class Task(
    val id: Long = 0L,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val date: LocalDate,
    val time: LocalTime? = null,
    val location: String? = null,
    val isCompleted: Boolean = false,
    val createdAt: LocalDateTime = LocalDateTime.now()
)
