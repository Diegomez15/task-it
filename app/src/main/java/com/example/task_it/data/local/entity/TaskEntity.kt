package com.example.task_it.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.task_it.domain.model.TaskPriority

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val title: String,
    val description: String,
    val priority: TaskPriority,
    val date: String,
    val time: String?,
    val location: String?,
    val isCompleted: Boolean,
    val createdAt: String
)
