package com.example.task_it.domain.repository

import com.example.task_it.domain.model.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun insertTask(task: Task)

    suspend fun updateTask(task: Task)

    suspend fun deleteTask(task: Task)

    fun getAllTasks(): Flow<List<Task>>
}
