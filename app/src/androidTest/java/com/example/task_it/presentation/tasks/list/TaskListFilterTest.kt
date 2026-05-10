package com.example.task_it.presentation.tasks.list

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime

class TaskListFilterTest {

    @Test
    fun whenPriorityFilterApplied_onlyMatchingTasksAreReturned() {

        val lowTask = Task(
            id = 1L,
            title = "Tarea baja",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.BAJA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val highTask = Task(
            id = 2L,
            title = "Tarea alta",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.ALTA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val result = filterAndSortTasks(
            tasks = listOf(lowTask, highTask),
            selectedPriority = TaskPriority.ALTA,
            searchQuery = ""
        )

        assertEquals(1, result.size)
        assertEquals("Tarea alta", result.first().title)
    }

    @Test
    fun whenSearchQueryMatchesTitle_ignoringCase_taskIsReturned() {
        val task1 = Task(
            id = 1L,
            title = "Comprar pan",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val task2 = Task(
            id = 2L,
            title = "Estudiar Kotlin",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val result = filterAndSortTasks(
            tasks = listOf(task1, task2),
            selectedPriority = null,
            searchQuery = "kotlin"
        )

        assertEquals(1, result.size)
        assertEquals("Estudiar Kotlin", result.first().title)
    }

    @Test
    fun tasksAreSortedByCompletedStateAndDate() {
        val completedEarlyTask = Task(
            id = 1L,
            title = "Completada mañana",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = true,
            createdAt = LocalDateTime.now()
        )

        val pendingLateTask = Task(
            id = 2L,
            title = "Pendiente tarde",
            description = "",
            date = LocalDate.now().plusDays(3),
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val pendingEarlyTask = Task(
            id = 3L,
            title = "Pendiente pronto",
            description = "",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = null,
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val result = filterAndSortTasks(
            tasks = listOf(completedEarlyTask, pendingLateTask, pendingEarlyTask),
            selectedPriority = null,
            searchQuery = ""
        )

        assertEquals(
            listOf(
                "Pendiente pronto",
                "Pendiente tarde",
                "Completada mañana"
            ),
            result.map { it.title }
        )
    }
}