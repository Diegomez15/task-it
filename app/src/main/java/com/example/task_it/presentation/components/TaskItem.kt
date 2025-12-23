package com.example.task_it.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.task_it.domain.model.Task
import com.example.task_it.presentation.theme.YellowPrimary
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun TaskItem(
    task: Task,
    onEdit: (Task) -> Unit,
    onDelete: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es", "ES"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // ✅ Hace que la altura del Row sea la del contenido
                .padding(14.dp)
        ) {

        // Barra lateral amarilla (post-it)
            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight() // ✅ Se adapta al alto real de la tarjeta
                    .clip(RoundedCornerShape(99.dp))
                    .background(YellowPrimary)
            )


            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 2.dp)
                            .padding(end = 8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    IconButton(onClick = { onEdit(task) }) {
                        Icon(Icons.Filled.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = { onDelete(task) }) {
                        Icon(Icons.Filled.DeleteOutline, contentDescription = "Eliminar")
                    }
                }


                // Fecha
                MetaRow(
                    icon = { Icon(Icons.Filled.Today, contentDescription = null) },
                    text = task.date.format(dateFormatter)
                )

                // Hora (opcional)
                task.time?.let {
                    MetaRow(
                        icon = { Icon(Icons.Filled.Schedule, contentDescription = null) },
                        text = it.format(timeFormatter)
                    )
                }

                // Ubicación (opcional)
                task.location?.takeIf { it.isNotBlank() }?.let {
                    MetaRow(
                        icon = { Icon(Icons.Filled.LocationOn, contentDescription = null) },
                        text = it
                    )
                }

                // Prioridad
                Text(
                    text = "Importancia: ${task.priority.name.lowercase().replaceFirstChar { c -> c.titlecase() }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun MetaRow(
    icon: @Composable () -> Unit,
    text: String
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
