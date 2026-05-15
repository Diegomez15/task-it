package com.example.task_it.presentation.calendar

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

fun getTasksForSelectedDate(
    tasks: List<Task>,
    selectedDate: LocalDate
): List<Task> {
    return tasks
        .filter { it.date == selectedDate }
        .sortedBy { it.time ?: LocalTime.MAX }
}

fun getDayMarkersForMonth(
    tasks: List<Task>,
    month: YearMonth
): Map<LocalDate, List<TaskPriority>> {
    val monthStart = month.atDay(1)
    val monthEnd = month.atEndOfMonth()

    return tasks
        .asSequence()
        .filter { it.date in monthStart..monthEnd }
        .groupBy { it.date }
        .mapValues { (_, dayTasks) ->
            dayTasks
                .filter { !it.isCompleted }
                .map { it.priority }
                .distinct()
        }
}