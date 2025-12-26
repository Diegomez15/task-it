package com.example.task_it.presentation.tasks.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_it.di.AppModule
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

class TaskFormViewModel(
    application: Application,
    private val taskId: Long?
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskFormUiState())
    val uiState: StateFlow<TaskFormUiState> = _uiState

    private val addTaskUseCase = AppModule.provideAddTaskUseCase(application)
    private val updateTaskUseCase = AppModule.provideUpdateTaskUseCase(application)
    private val getTaskByIdUseCase = AppModule.provideGetTaskByIdUseCase(application)

    // Guardamos la tarea original para preservar createdAt / isCompleted
    private var loadedTask: Task? = null

    init {
        taskId?.let { id ->
            viewModelScope.launch {
                val task = getTaskByIdUseCase(id) ?: return@launch
                loadedTask = task

                _uiState.value = _uiState.value.copy(
                    title = task.title,
                    description = task.description,
                    priority = task.priority,
                    date = task.date,
                    time = task.time,
                    location = task.location.orEmpty()
                )
            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(
            title = value.take(TaskFormLimits.TITLE_MAX)
        )
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(
            description = value.take(TaskFormLimits.DESCRIPTION_MAX)
        )
    }

    fun onPriorityChange(value: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = value)
    }

    fun onDateChange(value: LocalDate) {
        _uiState.value = _uiState.value.copy(date = value)
    }

    fun onTimeChange(value: LocalTime?) {
        _uiState.value = _uiState.value.copy(time = value)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(
            location = value.take(TaskFormLimits.LOCATION_MAX)
        )
    }

    /**
     * ✅ Un único submit:
     * - si taskId == null -> INSERT
     * - si taskId != null -> UPDATE
     */
    fun submitTask() {
        val state = _uiState.value
        val existing = loadedTask

        val task = Task(
            id = existing?.id ?: 0L,
            title = state.title.trim(),
            description = state.description.trim(),
            priority = state.priority,
            date = state.date,
            time = state.time,
            location = state.location.trim().ifBlank { null },
            isCompleted = existing?.isCompleted ?: false,
            createdAt = existing?.createdAt ?: LocalDateTime.now()
        )

        viewModelScope.launch {
            if (existing == null) addTaskUseCase(task)
            else updateTaskUseCase(task)
        }
    }
}
