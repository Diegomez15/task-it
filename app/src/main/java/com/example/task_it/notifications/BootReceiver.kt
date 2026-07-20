package com.example.task_it.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.task_it.di.AppModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val scheduler = TaskReminderScheduler(context.applicationContext)
                val tasks = AppModule
                    .provideGetTasksUseCase(context.applicationContext)
                    .invoke()
                    .first()

                tasks
                    .filter { task ->
                        !task.isCompleted && task.reminderMinutesBefore != null
                    }
                    .forEach { task ->
                        scheduler.schedule(task)
                    }
            } finally {
                pendingResult.finish()
            }
        }
    }
}