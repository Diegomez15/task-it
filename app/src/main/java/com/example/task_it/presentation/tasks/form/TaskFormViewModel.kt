package com.example.task_it.presentation.tasks.form

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_it.di.AppModule
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
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

    private fun validateDateTime(
        date: LocalDate,
        time: LocalTime?,
        originalDate: LocalDate?,
        originalTime: LocalTime?
    ): String? {
        val now = LocalDateTime.now()

        // ✅ MODO EDICIÓN: si el usuario NO ha cambiado fecha/hora,
        // permitimos que la tarea siga estando en el pasado.
        if (originalDate != null) {
            val sameDate = date == originalDate
            val sameTime = time == originalTime // contempla null == null
            if (sameDate && sameTime) return null
        }

        // A partir de aquí, aplicamos regla estricta (crear o editar cambiando fecha/hora)
        if (time == null) {
            return if (date.isBefore(now.toLocalDate())) {
                "No puedes seleccionar una fecha pasada"
            } else null
        }

        val selected = LocalDateTime.of(date, time)
        return if (selected.isBefore(now)) {
            "No puedes seleccionar una fecha y hora pasadas"
        } else null
    }


    private fun isFormValid(state: TaskFormUiState): Boolean {
        if (state.title.isBlank()) return false
        if (state.dateTimeError != null) return false
        if (state.date == null) return false
        return true
    }


    // Guardamos la tarea original para preservar createdAt / isCompleted
    private var loadedTask: Task? = null

    init {
        taskId?.let { id ->
            viewModelScope.launch {
                val task = getTaskByIdUseCase(id) ?: return@launch
                loadedTask = task

                val err = validateDateTime(
                    date = task.date,
                    time = task.time,
                    originalDate = task.date,
                    originalTime = task.time
                )


                _uiState.value = _uiState.value.copy(
                    title = task.title,
                    description = task.description,
                    priority = task.priority,
                    date = task.date,
                    time = task.time,
                    location = task.location.orEmpty(),
                    dateTimeError = err,
                    isSubmitEnabled = isFormValid(
                        _uiState.value.copy(
                            title = task.title,
                            date = task.date,
                            time = task.time,
                            dateTimeError = err
                        )
                    )
                )

            }
        }
    }

    fun onTitleChange(value: String) {
        _uiState.update { state ->
            val updatedState = state.copy(
                title = value.take(TaskFormLimits.TITLE_MAX)
            )
            updatedState.copy(
                isSubmitEnabled = isFormValid(updatedState)
            )
        }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { state ->
            val updatedState = state.copy(
                description = value.take(TaskFormLimits.DESCRIPTION_MAX)
            )
            updatedState.copy(
                isSubmitEnabled = isFormValid(updatedState)
            )
        }
    }

    fun onPriorityChange(value: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = value)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.update { state ->
            val err = validateDateTime(
                date = date,
                time = state.time,
                originalDate = loadedTask?.date,
                originalTime = loadedTask?.time
            )


            val updatedState = state.copy(
                date = date,
                dateTimeError = err
            )

            updatedState.copy(
                isSubmitEnabled = isFormValid(updatedState)
            )
        }
    }


    fun onTimeChange(time: LocalTime?) {
        _uiState.update { state ->
            val err = validateDateTime(
                date = state.date,
                time = time,
                originalDate = loadedTask?.date,
                originalTime = loadedTask?.time
            )


            val updatedState = state.copy(
                time = time,
                dateTimeError = err
            )

            updatedState.copy(
                isSubmitEnabled = isFormValid(updatedState)
            )
        }
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
        if (!state.isSubmitEnabled) return
        val existing = loadedTask


        val task = Task(
            id = existing?.id ?: 0L,
            title = state.title.trim(),
            description = state.description.trim(),
            priority = state.priority,
            date = state.date,
            time = state.time,
            location = state.location?.trim()?.ifBlank { null },
            isCompleted = existing?.isCompleted ?: false,
            createdAt = existing?.createdAt ?: LocalDateTime.now()
        )

        viewModelScope.launch {
            if (existing == null) addTaskUseCase(task)
            else updateTaskUseCase(task)
        }
    }


}
