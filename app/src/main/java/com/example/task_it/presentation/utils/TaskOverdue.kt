package com.example.task_it.presentation.utils

import com.example.task_it.domain.model.Task
import java.time.LocalDateTime

/**
 * Una tarea se considera atrasada si no está completada y su fecha/hora ya ha pasado.
 * - Si tiene hora: LocalDateTime(date, time) < now
 * - Si NO tiene hora: date < hoy
 */
fun Task.isOverdue(now: LocalDateTime = LocalDateTime.now()): Boolean {
    if (isCompleted) return false

    return if (time != null) {
        LocalDateTime.of(date, time).isBefore(now)
    } else {
        date.isBefore(now.toLocalDate())
    }
}
