package com.example.task_it.di

import android.content.Context
import androidx.room.Room
import com.example.task_it.data.local.database.AppDatabase
import com.example.task_it.data.repository.TaskRepositoryImpl
import com.example.task_it.domain.repository.TaskRepository
import com.example.task_it.domain.usecase.AddTaskUseCase
import com.example.task_it.domain.usecase.GetTasksUseCase
import com.example.task_it.domain.usecase.DeleteTaskUseCase
import com.example.task_it.domain.usecase.GetTaskByIdUseCase
import com.example.task_it.domain.usecase.UpdateTaskUseCase


object AppModule {

    private lateinit var database: AppDatabase

    fun provideDatabase(context: Context): AppDatabase {
        if (!::database.isInitialized) {
            database = Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "taskit_db"
            ).build()
        }
        return database
    }

    fun provideTaskRepository(context: Context): TaskRepository {
        return TaskRepositoryImpl(provideDatabase(context).taskDao())
    }

    fun provideAddTaskUseCase(context: Context): AddTaskUseCase =
        AddTaskUseCase(provideTaskRepository(context))

    fun provideGetTasksUseCase(context: Context): GetTasksUseCase =
        GetTasksUseCase(provideTaskRepository(context))

    fun provideDeleteTaskUseCase(context: Context): DeleteTaskUseCase =
        DeleteTaskUseCase(provideTaskRepository(context))

    fun provideUpdateTaskUseCase(context: Context): UpdateTaskUseCase =
        UpdateTaskUseCase(provideTaskRepository(context))

    fun provideGetTaskByIdUseCase(context: Context): GetTaskByIdUseCase =
        GetTaskByIdUseCase(provideTaskRepository(context))

}
