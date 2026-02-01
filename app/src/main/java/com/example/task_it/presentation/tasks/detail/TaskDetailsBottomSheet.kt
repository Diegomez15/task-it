package com.example.task_it.presentation.tasks.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.format.DateTimeFormatter
import com.example.task_it.domain.model.Task
import com.example.task_it.presentation.theme.YellowPrimary
import com.example.task_it.presentation.theme.color
import com.example.task_it.presentation.utils.isOverdue



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsBottomSheet(
    task: Task,
    onDismiss: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val dateText = remember(task.date) {
        // Ajusta el formato a lo que ya uses en tu app si quieres
        task.date.format(DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy"))
    }
    val timeText = remember(task.time) {
        task.time?.format(DateTimeFormatter.ofPattern("HH:mm"))
    }

    val priorityColor = task.priority.color()
    val isOverdue = task.isOverdue()


    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
        dragHandle = { }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: prioridad + cerrar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // punto de color
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(priorityColor)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "PRIORIDAD ${task.priority.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Cerrar")
                }
            }

            // Título
            Text(
                text = task.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            val description = task.description.trim()
            if (description.isNotBlank()) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }


            // Datos
            TaskDetailRow(
                icon = Icons.Outlined.Today,
                label = "Fecha",
                value = buildString {
                    append(dateText)
                    if (isOverdue) append(" (atrasada)")
                },
                valueColor = if (isOverdue)
                    MaterialTheme.colorScheme.error
                else
                    MaterialTheme.colorScheme.onSurface
            )


            if (timeText != null) {
                TaskDetailRow(
                    icon = Icons.Outlined.Schedule,
                    label = "Hora",
                    value = timeText
                )
            }

            val location = task.location?.takeIf { it.isNotBlank() }
            if (location != null) {
                TaskDetailRow(
                    icon = Icons.Outlined.LocationOn,
                    label = "Ubicación",
                    value = location
                )
            }

            Spacer(Modifier.height(6.dp))

            // Botones
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Outlined.Delete, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Borrar")
                }

                Button(
                    onClick = onEdit,
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowPrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Icon(Icons.Outlined.Edit, contentDescription = null)
                    Spacer(Modifier.width(8.dp))
                    Text("Editar")
                }
            }

            Spacer(Modifier.height(10.dp))
        }
    }
}

@Composable
private fun TaskDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = valueColor
            )
        }
    }
}

