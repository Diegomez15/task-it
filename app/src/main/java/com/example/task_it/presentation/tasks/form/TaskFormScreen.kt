package com.example.task_it.presentation.tasks.form

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task_it.domain.model.TaskPriority
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun TaskFormScreen(
    onCancel: () -> Unit,
    onCreateTask: () -> Unit
) {
    val context = LocalContext.current

    // ViewModel con Application (AndroidViewModel)
    val viewModel: TaskFormViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskFormViewModel(context.applicationContext as Application) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

    val dateFormatter = remember { DateTimeFormatter.ofPattern("MM / dd / yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    val datePickerDialog = remember {
        DatePickerDialog(
            context,
            { _, year, month, day ->
                viewModel.onDateChange(LocalDate.of(year, month + 1, day))
            },
            state.date.year,
            state.date.monthValue - 1,
            state.date.dayOfMonth
        )
    }

    val timePickerDialog = remember {
        TimePickerDialog(
            context,
            { _, hour, minute ->
                viewModel.onTimeChange(LocalTime.of(hour, minute))
            },
            state.time?.hour ?: LocalTime.now().hour,
            state.time?.minute ?: LocalTime.now().minute,
            true
        )
    }

    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Cabecera similar a la captura
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Nueva tarea",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Completa los detalles de tu nueva tarea",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onCancel) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sección Título
            FormSectionCard(title = "Título") {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = viewModel::onTitleChange,
                    label = { Text("Título") },
                    supportingText = {
                        Text("${state.title.length}/${TaskFormLimits.TITLE_MAX}")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormSectionCard(title = "Descripción") {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = viewModel::onDescriptionChange,
                    label = { Text("Descripción") },
                    supportingText = {
                        Text("${state.description.length}/${TaskFormLimits.DESCRIPTION_MAX}")
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección Nivel de importancia
            FormSectionCard(title = "Nivel de importancia") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityRow(
                        selected = state.priority,
                        onPrioritySelected = { viewModel.onPriorityChange(it)}
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección Fecha y hora
            FormSectionCard(title = "Fecha y hora") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Fecha",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { datePickerDialog.show() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(state.date.format(dateFormatter))
                        }
                    }

                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Hora",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedButton(
                            onClick = { timePickerDialog.show() },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.surfaceContainer)
                        ) {
                            Text(state.time?.format(timeFormatter) ?: "--:--")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sección Ubicación (opcional)
            FormSectionCard(title = "Ubicación (opcional)") {
                OutlinedTextField(
                    value = state.location ?: "",
                    onValueChange = viewModel::onLocationChange,
                    label = { Text("Ubicación (opcional)") },
                    supportingText = {
                        val text = state.location ?: ""
                        Text("${text.length}/${TaskFormLimits.LOCATION_MAX}")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

            }

            Spacer(modifier = Modifier.height(24.dp))

            // Botones inferiores: Cancelar / Crear tarea
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton (
                    onClick = onCancel,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    )
                ) {
                    Text("Cancelar")
                }

                val isCreateEnabled = state.title.trim().isNotEmpty()


                Button(
                    onClick = {
                        viewModel.createTask()
                        onCreateTask()
                    },
                    modifier = Modifier.weight(1f),
                    enabled = isCreateEnabled
                ) {
                    Text("Crear tarea")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// Card contenedora de cada bloque, similar a tu diseño original
@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            content()
        }
    }
}


// Chips para prioridad (manteniendo tu cambio)
@Composable
private fun PriorityRow(
    selected: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        PriorityChip(
            label = "Crítica",
            selected = selected == TaskPriority.CRITICA,
            onClick = { onPrioritySelected(TaskPriority.CRITICA) }
        )
        PriorityChip(
            label = "Alta",
            selected = selected == TaskPriority.ALTA,
            onClick = { onPrioritySelected(TaskPriority.ALTA) }
        )
        PriorityChip(
            label = "Media",
            selected = selected == TaskPriority.MEDIA,
            onClick = { onPrioritySelected(TaskPriority.MEDIA) }
        )
        PriorityChip(
            label = "Baja",
            selected = selected == TaskPriority.BAJA,
            onClick = { onPrioritySelected(TaskPriority.BAJA) }
        )
    }
}

@Composable
private fun PriorityChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
            containerColor = MaterialTheme.colorScheme.surface
        )
    )
}
