package com.example.task_it.presentation.tasks.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_it.di.AppModule
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.usecase.DeleteTaskUseCase
import com.example.task_it.domain.usecase.GetTasksUseCase
import com.example.task_it.domain.usecase.UpdateTaskUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class TaskListViewModel(
    application: Application,
    private val getTasksUseCase: GetTasksUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : AndroidViewModel(application) {

    constructor(application: Application) : this(
        application = application,
        getTasksUseCase = AppModule.provideGetTasksUseCase(application),
        deleteTaskUseCase = AppModule.provideDeleteTaskUseCase(application),
        updateTaskUseCase = AppModule.provideUpdateTaskUseCase(application)
    )

    val tasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase(task)
        }
    }

    fun toggleTaskCompleted(task: Task) {
        viewModelScope.launch {
            updateTaskUseCase(
                task.copy(isCompleted = !task.isCompleted)
            )
        }
    }
}