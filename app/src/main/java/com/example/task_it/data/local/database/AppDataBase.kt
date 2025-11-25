package com.example.task_it.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.task_it.data.local.dao.TaskDao
import com.example.task_it.data.local.entity.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
