package com.example.task_it.presentation.tasks.form

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import java.time.LocalDate
import java.time.LocalTime
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.example.task_it.domain.usecase.AddTaskUseCase
import com.example.task_it.domain.usecase.UpdateTaskUseCase
import com.example.task_it.domain.usecase.GetTaskByIdUseCase
import kotlinx.coroutines.test.runTest
import com.example.task_it.domain.model.TaskPriority
import java.time.LocalDateTime
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.Rule
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@OptIn(ExperimentalCoroutinesApi::class)
class TaskFormViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private fun createViewModel(
        repository: TaskRepository = FakeTaskRepository(),
        taskId: Long? = null
    ): TaskFormViewModel {
        val application = ApplicationProvider.getApplicationContext<Application>()

        return TaskFormViewModel(
            application = application,
            taskId = taskId,
            addTaskUseCase = AddTaskUseCase(repository),
            updateTaskUseCase = UpdateTaskUseCase(repository),
            getTaskByIdUseCase = GetTaskByIdUseCase(repository)
        )
    }

    private class FakeTaskRepository(
        private val taskToReturn: Task? = null
    ) : TaskRepository {

        val insertedTasks = mutableListOf<Task>()
        val updatedTasks = mutableListOf<Task>()

        override suspend fun insertTask(task: Task) {
            insertedTasks.add(task)
        }

        override suspend fun updateTask(task: Task) {
            updatedTasks.add(task)
        }

        override suspend fun deleteTask(task: Task) {
            // No necesario para estos tests
        }

        override suspend fun getTaskById(id: Long): Task? {
            return taskToReturn?.takeIf { it.id == id }
        }

        override fun getAllTasks(): Flow<List<Task>> {
            return flowOf(insertedTasks + updatedTasks)
        }
    }

    @Test
    fun whenTitleIsEmpty_submitIsDisabled() {
        val viewModel = createViewModel()

        viewModel.onTitleChange("")

        val state = viewModel.uiState.value

        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenTitleIsValid_submitIsEnabled() {
        val viewModel = createViewModel()

        viewModel.onTitleChange("Comprar pan")

        val state = viewModel.uiState.value

        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenTitleExceedsMaxLength_titleIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longTitle = "A".repeat(TaskFormLimits.TITLE_MAX + 10)

        viewModel.onTitleChange(longTitle)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.TITLE_MAX, state.title.length)
    }

    @Test
    fun whenTitleChanges_submitStateUpdatesCorrectly() {
        val viewModel = createViewModel()

        // Inicialmente vacío → deshabilitado
        var state = viewModel.uiState.value
        assertFalse(state.isSubmitEnabled)

        // Usuario escribe título válido
        viewModel.onTitleChange("Comprar pan")

        state = viewModel.uiState.value

        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenDescriptionExceedsMaxLength_descriptionIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longDescription = "A".repeat(TaskFormLimits.DESCRIPTION_MAX + 10)

        viewModel.onDescriptionChange(longDescription)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.DESCRIPTION_MAX, state.description.length)
    }

    @Test
    fun whenDateIsInPast_dateTimeErrorIsShownAndSubmitIsDisabled() {
        val viewModel = createViewModel()

        val pastDate = LocalDate.now().minusDays(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(pastDate)

        val state = viewModel.uiState.value

        assertEquals("No puedes seleccionar una fecha pasada", state.dateTimeError)
        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenTodayTimeIsInPast_dateTimeErrorIsShownAndSubmitIsDisabled() {
        val viewModel = createViewModel()

        val today = LocalDate.now()
        val pastTime = LocalTime.now().minusHours(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(today)
        viewModel.onTimeChange(pastTime)

        val state = viewModel.uiState.value

        assertEquals("No puedes seleccionar una fecha y hora pasadas", state.dateTimeError)
        assertFalse(state.isSubmitEnabled)
    }

    @Test
    fun whenDateIsInFuture_dateTimeErrorIsNullAndSubmitIsEnabled() {
        val viewModel = createViewModel()

        val futureDate = LocalDate.now().plusDays(1)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDateChange(futureDate)

        val state = viewModel.uiState.value

        assertEquals(null, state.dateTimeError)
        assertTrue(state.isSubmitEnabled)
    }

    @Test
    fun whenLocationExceedsMaxLength_locationIsTrimmedToMaxLength() {
        val viewModel = createViewModel()

        val longLocation = "A".repeat(TaskFormLimits.LOCATION_MAX + 10)

        viewModel.onLocationChange(longLocation)

        val state = viewModel.uiState.value

        assertEquals(TaskFormLimits.LOCATION_MAX, state.location?.length)
    }

    @Test
    fun whenSubmitTaskWithValidForm_taskIsInserted() = runTest {
        val repository = FakeTaskRepository()
        val viewModel = createViewModel(repository)

        viewModel.onTitleChange("Comprar pan")
        viewModel.onDescriptionChange("Ir al supermercado")
        viewModel.onLocationChange("Madrid")
        viewModel.onDateChange(LocalDate.now().plusDays(1))

        viewModel.submitTask()
        advanceUntilIdle()

        assertEquals(1, repository.insertedTasks.size)

        val insertedTask = repository.insertedTasks.first()

        assertEquals("Comprar pan", insertedTask.title)
        assertEquals("Ir al supermercado", insertedTask.description)
        assertEquals("Madrid", insertedTask.location)
    }

    @Test
    fun whenEditMode_loadsExistingTaskData() = runTest {
        val existingTask = Task(
            id = 1L,
            title = "Tarea original",
            description = "Descripción original",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val repository = FakeTaskRepository(
            taskToReturn = existingTask
        )

        val viewModel = createViewModel(
            repository = repository,
            taskId = 1L
        )

        advanceUntilIdle()

        val state = viewModel.uiState.value

        assertEquals("Tarea original", state.title)
        assertEquals("Descripción original", state.description)
        assertEquals("Madrid", state.location)
        assertEquals(existingTask.date, state.date)
    }

    @Test
    fun whenSubmitInEditMode_taskIsUpdatedNotInserted() = runTest {
        val existingTask = Task(
            id = 1L,
            title = "Tarea original",
            description = "Descripción original",
            date = LocalDate.now().plusDays(1),
            time = null,
            location = "Madrid",
            priority = TaskPriority.MEDIA,
            isCompleted = false,
            createdAt = LocalDateTime.now()
        )

        val repository = FakeTaskRepository(
            taskToReturn = existingTask
        )

        val viewModel = createViewModel(
            repository = repository,
            taskId = 1L
        )

        // Esperamos a que cargue la tarea
        advanceUntilIdle()

        // Modificamos datos
        viewModel.onTitleChange("Tarea editada")

        // Ejecutamos submit
        viewModel.submitTask()
        advanceUntilIdle()

        // Validaciones
        assertEquals(0, repository.insertedTasks.size)
        assertEquals(1, repository.updatedTasks.size)

        val updatedTask = repository.updatedTasks.first()

        assertEquals("Tarea editada", updatedTask.title)
    }

}

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