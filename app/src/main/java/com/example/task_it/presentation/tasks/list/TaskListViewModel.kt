package com.example.task_it.presentation.tasks.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.task_it.di.AppModule
import com.example.task_it.domain.model.Task
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class TaskListViewModel(application: Application) : AndroidViewModel(application) {

    private val getTasksUseCase = AppModule.provideGetTasksUseCase(application)

    val tasks: StateFlow<List<Task>> = getTasksUseCase()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
