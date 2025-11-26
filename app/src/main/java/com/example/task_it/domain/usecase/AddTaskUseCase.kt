package com.example.task_it.domain.usecase

import com.example.task_it.domain.model.Task
import com.example.task_it.domain.repository.TaskRepository

class AddTaskUseCase(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: Task) {
        repository.insertTask(task)
    }
}
