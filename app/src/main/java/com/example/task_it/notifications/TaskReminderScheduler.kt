package com.example.task_it.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.task_it.domain.model.Task
import java.time.LocalDateTime
import java.time.ZoneId

class TaskReminderScheduler(
    private val context: Context
) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)

    fun schedule(task: Task) {
        val reminderTimeMillis = task.reminderTimeMillis() ?: return
        if (reminderTimeMillis <= System.currentTimeMillis()) return

        alarmManager.setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            reminderTimeMillis,
            task.pendingIntent()
        )
    }

    fun cancel(taskId: Long) {
        alarmManager.cancel(pendingIntent(taskId))
    }

    private fun Task.reminderTimeMillis(): Long? {
        val time = time ?: return null
        val minutesBefore = reminderMinutesBefore ?: return null

        return LocalDateTime.of(date, time)
            .minusMinutes(minutesBefore.toLong())
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
    }

    private fun Task.pendingIntent(): PendingIntent {
        return pendingIntent(id)
    }

    private fun pendingIntent(taskId: Long): PendingIntent {
        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(TaskReminderReceiver.EXTRA_TASK_ID, taskId)
        }

        return PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}