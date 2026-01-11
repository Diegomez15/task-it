package com.example.task_it.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Today
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.task_it.domain.model.Task
import com.example.task_it.presentation.theme.YellowPrimary
import java.time.format.DateTimeFormatter
import java.util.Locale
import com.example.task_it.presentation.theme.color


@Composable
fun TaskItem(
    task: Task,
    onToggleCompleted: (Task) -> Unit,
    modifier: Modifier = Modifier
)
{
    val dateFormatter = DateTimeFormatter.ofPattern("d 'de' MMMM, yyyy", Locale("es", "ES"))
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

    val contentAlpha = if (task.isCompleted) 0.45f else 1f

    ElevatedCard(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        //Barra decorativa
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(14.dp)
        ) {
            val stripeAlpha = if (task.isCompleted) 0.45f else 1f

            val stripeColor = task.priority.color()

            Box(
                modifier = Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(99.dp))
                    .alpha(stripeAlpha) // se atenúa al completar
                    .background(stripeColor)
            )


            Spacer(modifier = Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(contentAlpha),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {


                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 2.dp)
                            .padding(end = 8.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )



                    IconButton(
                        onClick = { onToggleCompleted(task) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = if (task.isCompleted)
                                Icons.Filled.CheckCircle
                            else
                                Icons.Outlined.RadioButtonUnchecked,
                            contentDescription = if (task.isCompleted)
                                "Marcar como pendiente"
                            else
                                "Marcar como completada",
                            tint = if (task.isCompleted)
                                YellowPrimary
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
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

                // Importancia
                Text(
                    text = "Importancia: ${task.priority.label}",
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
