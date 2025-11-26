package com.example.task_it.domain.usecase

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow

class GetTasksUseCase(
    private val repository: TaskRepository
) {
    operator fun invoke(): Flow<List<Task>> = repository.getAllTasks()
}
