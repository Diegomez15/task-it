package com.example.task_it.presentation.tasks.list

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.repository.TaskRepository
import com.example.task_it.domain.usecase.DeleteTaskUseCase
import com.example.task_it.domain.usecase.GetTasksUseCase
import com.example.task_it.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description
import com.example.task_it.domain.model.TaskPriority
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.collect

@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val testDispatcher: TestDispatcher = StandardTestDispatcher()
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TaskListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private class FakeTaskRepository : TaskRepository {

        private val tasksFlow = MutableStateFlow<List<Task>>(emptyList())

        val deletedTasks = mutableListOf<Task>()
        val updatedTasks = mutableListOf<Task>()

        fun setTasks(tasks: List<Task>) {
            tasksFlow.value = tasks
        }

        override suspend fun insertTask(task: Task) {
            tasksFlow.value += task
        }

        override suspend fun updateTask(task: Task) {
            updatedTasks.add(task)
        }

        override suspend fun deleteTask(task: Task) {
            deletedTasks.add(task)
        }

        override suspend fun getTaskById(id: Long): Task? {
            return tasksFlow.value.find { it.id == id }
        }

        override fun getAllTasks(): Flow<List<Task>> {
            return tasksFlow.asStateFlow()
        }
    }

    private fun createViewModel(
        repository: TaskRepository
    ): TaskListViewModel {
        val application = ApplicationProvider.getApplicationContext<Application>()

        return TaskListViewModel(
            application = application,
            getTasksUseCase = GetTasksUseCase(repository),
            deleteTaskUseCase = DeleteTaskUseCase(repository),
            updateTaskUseCase = UpdateTaskUseCase(repository)
        )
    }

    @Test
    fun whenRepositoryHasTasks_viewModelExposesTasks() = runTest {
        val repository = FakeTaskRepository()

        val task = Task(
            id = 1L,
            title = "Comprar pan",
            description = "Ir al supermercado",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        repository.setTasks(listOf(task))

        val viewModel = createViewModel(repository)

        val collectJob = launch {
            viewModel.tasks.collect { }
        }

        advanceUntilIdle()

        val tasks = viewModel.tasks.value

        assertEquals(1, tasks.size)
        assertEquals("Comprar pan", tasks.first().title)

        collectJob.cancel()
    }

    @Test
    fun whenToggleTaskCompleted_updatesTaskWithOppositeCompletedState() = runTest {
        val repository = FakeTaskRepository()

        val task = Task(
            id = 1L,
            title = "Comprar pan",
            description = "Ir al supermercado",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val viewModel = createViewModel(repository)

        viewModel.toggleTaskCompleted(task)

        advanceUntilIdle()

        assertEquals(1, repository.updatedTasks.size)

        val updatedTask = repository.updatedTasks.first()

        assertEquals(task.id, updatedTask.id)
        assertEquals(true, updatedTask.isCompleted)
    }

    @Test
    fun whenToggleCompletedTask_updatesTaskAsPending() = runTest {
        val repository = FakeTaskRepository()

        val task = Task(
            id = 1L,
            title = "Comprar pan",
            description = "Ir al supermercado",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = true,
            createdAt = LocalDateTime.now()
        )

        val viewModel = createViewModel(repository)

        viewModel.toggleTaskCompleted(task)

        advanceUntilIdle()

        assertEquals(1, repository.updatedTasks.size)

        val updatedTask = repository.updatedTasks.first()

        assertEquals(task.id, updatedTask.id)
        assertEquals(false, updatedTask.isCompleted)
    }

    @Test
    fun whenDeleteTask_taskIsDeleted() = runTest {
        val repository = FakeTaskRepository()

        val task = Task(
            id = 1L,
            title = "Comprar pan",
            description = "Ir al supermercado",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val viewModel = createViewModel(repository)

        viewModel.deleteTask(task)

        advanceUntilIdle()

        assertEquals(1, repository.deletedTasks.size)
        assertEquals(task.id, repository.deletedTasks.first().id)
    }
}