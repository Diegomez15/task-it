package com.example.task_it.presentation.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_it.di.AppModule
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth

data class CalendarUiState(
    val currentMonth: YearMonth = YearMonth.now(),
    val selectedDate: LocalDate = LocalDate.now(),
    val tasksForSelectedDate: List<Task> = emptyList(),
    val dayMarkers: Map<LocalDate, List<TaskPriority>> = emptyMap()
)

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val getTasksUseCase = AppModule.provideGetTasksUseCase(application)
    private val tasksFlow = getTasksUseCase()

    private val _state = MutableStateFlow(CalendarUiState())
    val state: StateFlow<CalendarUiState> = _state.asStateFlow()
    private val deleteTaskUseCase = AppModule.provideDeleteTaskUseCase(application)
    private val updateTaskUseCase = AppModule.provideUpdateTaskUseCase(application)


    init {
        viewModelScope.launch {
            val monthFlow = _state.map { it.currentMonth }.distinctUntilChanged()
            val selectedFlow = _state.map { it.selectedDate }.distinctUntilChanged()

            combine(tasksFlow, monthFlow, selectedFlow) { tasks, month, selected ->

                val markers = getDayMarkersForMonth(
                    tasks = tasks,
                    month = month
                )

                val daily = getTasksForSelectedDate(
                    tasks = tasks,
                    selectedDate = selected
                )

                markers to daily
            }.collect { (markers, daily) ->
                _state.update {
                    it.copy(
                        dayMarkers = markers,
                        tasksForSelectedDate = daily
                    )
                }
            }
        }
    }

    fun onSelectDate(date: LocalDate) {
        _state.update { it.copy(selectedDate = date) }
    }

    fun onPrevMonth() {
        _state.update { it.copy(currentMonth = it.currentMonth.minusMonths(1)) }
    }

    fun onNextMonth() {
        _state.update { it.copy(currentMonth = it.currentMonth.plusMonths(1)) }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch { deleteTaskUseCase(task) }
    }

    fun toggleCompleted(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(task.copy(isCompleted = !task.isCompleted))
        }
    }

}
