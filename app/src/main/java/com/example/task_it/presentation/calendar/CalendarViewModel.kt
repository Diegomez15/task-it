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

    init {
        viewModelScope.launch {
            val monthFlow = _state.map { it.currentMonth }.distinctUntilChanged()
            val selectedFlow = _state.map { it.selectedDate }.distinctUntilChanged()

            combine(tasksFlow, monthFlow, selectedFlow) { tasks, month, selected ->

                val monthStart = month.atDay(1)
                val monthEnd = month.atEndOfMonth()

                // ✅ markers del mes (1 punto por prioridad presente)
                val markers: Map<LocalDate, List<TaskPriority>> = tasks
                    .asSequence()
                    .filter { it.date in monthStart..monthEnd }
                    .groupBy { it.date }
                    .mapValues { (_, dayTasks) ->
                        dayTasks
                            .filter { !it.isCompleted } // si quieres incluir completadas, quita este filtro
                            .map { it.priority }
                            .distinct()
                    }

                // ✅ tareas del día seleccionado
                val daily: List<Task> = tasks
                    .filter { it.date == selected }
                    .sortedBy { it.time ?: LocalTime.MAX }

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

    private val deleteTaskUseCase = AppModule.provideDeleteTaskUseCase(application)

    fun deleteTask(task: Task) {
        viewModelScope.launch { deleteTaskUseCase(task) }
    }

}
