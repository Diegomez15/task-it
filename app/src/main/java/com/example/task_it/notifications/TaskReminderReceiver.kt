package com.example.task_it.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.task_it.R
import com.example.task_it.di.AppModule
import com.example.task_it.presentation.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.task_it.domain.model.Task
import java.time.Duration
import java.time.LocalDateTime

class TaskReminderReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(EXTRA_TASK_ID, -1L)
        if (taskId == -1L) return

        val pendingResult = goAsync()

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val task = AppModule
                    .provideGetTaskByIdUseCase(context.applicationContext)
                    .invoke(taskId)

                if (task != null && !task.isCompleted) {
                    createNotificationChannel(context)
                    showNotification(context, task)
                }
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun showNotification(
        context: Context,
        task: Task
    ) {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        val openTaskIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TASK_ID, task.id)
        }

        val openTaskPendingIntent = PendingIntent.getActivity(
            context,
            task.id.toInt(),
            openTaskIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationText = buildTimeRemainingText(task)

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification_task)
            .setContentTitle(task.title)
            .setContentText(notificationText)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(openTaskPendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(task.id.toInt(), notification)
    }

    private fun buildTimeRemainingText(task: Task): String {
        val taskTime = task.time ?: return "Tienes una tarea pendiente"

        val dueDateTime = LocalDateTime.of(task.date, taskTime)
        val duration = Duration.between(LocalDateTime.now(), dueDateTime)

        if (duration.isNegative || duration.isZero) {
            return "La tarea debe completarse ahora"
        }

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        return when {
            days > 0 -> "Faltan $days d ${hours} h para completarla"
            hours > 0 -> "Faltan $hours h ${minutes} min para completarla"
            else -> "Faltan $minutes min para completarla"
        }
    }

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            "Recordatorios de tareas",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notificaciones para recordar tareas programadas"
        }

        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        const val EXTRA_TASK_ID = "extra_task_id"
        private const val CHANNEL_ID = "task_reminders"
    }
}