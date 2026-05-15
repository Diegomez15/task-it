package com.example.task_it.presentation.calendar

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth

class CalendarLogicTest {

    @Test
    fun whenSelectedDateHasTasks_onlyTasksFromThatDateAreReturned() {
        val selectedDate = LocalDate.of(2026, 5, 15)
        val otherDate = LocalDate.of(2026, 5, 16)

        val taskForSelectedDate = Task(
            id = 1L,
            title = "Tarea del día seleccionado",
            description = "",
            date = selectedDate,
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val taskForOtherDate = Task(
            id = 2L,
            title = "Tarea de otro día",
            description = "",
            date = otherDate,
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val result = getTasksForSelectedDate(
            tasks = listOf(taskForSelectedDate, taskForOtherDate),
            selectedDate = selectedDate
        )

        assertEquals(1, result.size)
        assertEquals("Tarea del día seleccionado", result.first().title)
    }

    @Test
    fun whenTasksHaveTime_tasksAreSortedByTimeAndTasksWithoutTimeGoLast() {
        val selectedDate = LocalDate.of(2026, 5, 15)

        val taskWithoutTime = Task(
            id = 1L,
            title = "Tarea sin hora",
            description = "",
            date = selectedDate,
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val afternoonTask = Task(
            id = 2L,
            title = "Tarea de tarde",
            description = "",
            date = selectedDate,
            time = LocalTime.of(18, 0),
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val morningTask = Task(
            id = 3L,
            title = "Tarea de mañana",
            description = "",
            date = selectedDate,
            time = LocalTime.of(9, 0),
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val result = getTasksForSelectedDate(
            tasks = listOf(taskWithoutTime, afternoonTask, morningTask),
            selectedDate = selectedDate
        )

        assertEquals(
            listOf(
                "Tarea de mañana",
                "Tarea de tarde",
                "Tarea sin hora"
            ),
            result.map { it.title }
        )
    }

    @Test
    fun whenMonthHasTasks_markersAreGeneratedOnlyForPendingTasksInThatMonth() {
        val month = YearMonth.of(2026, 5)
        val dayInMonth = LocalDate.of(2026, 5, 15)
        val dayOutOfMonth = LocalDate.of(2026, 6, 1)

        val pendingTask = Task(
            id = 1L,
            title = "Pendiente",
            description = "",
            date = dayInMonth,
            time = null,
            location = null,
            priority = TaskPriority.ALTA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val completedTask = Task(
            id = 2L,
            title = "Completada",
            description = "",
            date = dayInMonth,
            time = null,
            location = null,
            priority = TaskPriority.BAJA,
            isCompleted = true,
            createdAt = LocalDateTime.now()
        )

        val taskOutOfMonth = Task(
            id = 3L,
            title = "Fuera del mes",
            description = "",
            date = dayOutOfMonth,
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val markers = getDayMarkersForMonth(
            tasks = listOf(pendingTask, completedTask, taskOutOfMonth),
            month = month
        )

        assertEquals(1, markers.size)
        assertEquals(listOf(TaskPriority.ALTA), markers[dayInMonth])
    }
}