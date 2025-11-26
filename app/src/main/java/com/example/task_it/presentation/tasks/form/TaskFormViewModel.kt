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
import java.time.LocalTime

class TaskFormViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(TaskFormUiState())
    val uiState: StateFlow<TaskFormUiState> = _uiState

    private val addTaskUseCase = AppModule.provideAddTaskUseCase(application)

    fun onTitleChange(value: String) {
        _uiState.value = _uiState.value.copy(title = value)
    }

    fun onPriorityChange(value: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = value)
    }

    fun onDateChange(date: LocalDate) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun onTimeChange(time: LocalTime) {
        _uiState.value = _uiState.value.copy(time = time)
    }

    fun onLocationChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun onDescriptionChange(value: String) {
        _uiState.value = _uiState.value.copy(location = value)
    }

    fun createTask() {
        val state = _uiState.value

        val task = Task(
            title = state.title,
            description = "Sin descripción",
            priority = state.priority,
            date = state.date,
            time = state.time,
            location = state.location,
            isCompleted = false
        )

        viewModelScope.launch {
            addTaskUseCase(task)
        }
    }
}
