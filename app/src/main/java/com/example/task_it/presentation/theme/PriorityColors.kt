package com.example.task_it.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.task_it.domain.model.TaskPriority

@Composable
fun TaskPriority.color(): Color {
    return when (this) {
        TaskPriority.BAJA -> Color(0xFF2E7D32)     // verde
        TaskPriority.MEDIA -> Color(0xFFF4B400)    // amarillo
        TaskPriority.ALTA -> Color(0xFFFF9800)     // naranja
        TaskPriority.CRITICA -> Color(0xFFD32F2F)  // rojo
    }
}
