package com.example.task_it.data.mapper

import com.example.task_it.data.local.entity.TaskEntity
import com.example.task_it.domain.model.Task
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

fun TaskEntity.toDomain(): Task {
    return Task(
        id = id,
        title = title,
        description = description,
        priority = priority,
        date = LocalDate.parse(date),
        time = time?.let { LocalTime.parse(it) },
        location = location,
        isCompleted = isCompleted,
        createdAt = LocalDateTime.parse(createdAt)
    )
}

fun Task.toEntity(): TaskEntity {
    return TaskEntity(
        id = id,
        title = title,
        description = description,
        priority = priority,
        date = date.toString(),
        time = time?.toString(),
        location = location,
        isCompleted = isCompleted,
        createdAt = createdAt.toString()
    )
}
