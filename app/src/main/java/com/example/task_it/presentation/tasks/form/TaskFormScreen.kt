package com.example.task_it.presentation.tasks.form

import android.app.Application
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task_it.domain.model.TaskPriority
import com.example.task_it.presentation.components.PriorityChip
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.ui.platform.testTag


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormScreen(
    taskId: Long? = null,
    onCancel: () -> Unit,
    onCreateTask: () -> Unit
) {
    val context = LocalContext.current

    val viewModel: TaskFormViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return TaskFormViewModel(
                    context.applicationContext as Application,
                    taskId = taskId
                ) as T
            }
        }
    )

    val state by viewModel.uiState.collectAsState()
    val isEditMode = taskId != null

    val datePickerDialog = remember(state.date) {
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

    val timePickerDialog = remember(state.time) {
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

    TaskFormContent(
        state = state,
        isEditMode = isEditMode,
        onTitleChange = viewModel::onTitleChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onPriorityChange = viewModel::onPriorityChange,
        onDateClick = { datePickerDialog.show() },
        onTimeClick = { timePickerDialog.show() },
        onReminderChange = viewModel::onReminderChange,
        onLocationChange = viewModel::onLocationChange,
        onCancel = onCancel,
        onSubmit = {
            viewModel.submitTask()
            onCreateTask()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskFormContent(
    state: TaskFormUiState,
    isEditMode: Boolean,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onPriorityChange: (TaskPriority) -> Unit,
    onDateClick: () -> Unit,
    onTimeClick: () -> Unit,
    onReminderChange: (Int?) -> Unit = {},
    onLocationChange: (String) -> Unit,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dateFormatter = remember { DateTimeFormatter.ofPattern("dd / MM / yyyy") }
    val timeFormatter = remember { DateTimeFormatter.ofPattern("HH:mm") }

    var reminderMenuExpanded by remember { mutableStateOf(false) }

    val reminderOptions = remember {
        listOf(
            0 to "A la hora de la tarea",
            10 to "10 minutos antes",
            30 to "30 minutos antes",
            60 to "1 hora antes",
            1440 to "1 día antes"
        )
    }

    val reminderEnabled = state.reminderMinutesBefore != null

    val selectedReminderLabel = reminderOptions
        .firstOrNull { it.first == state.reminderMinutesBefore }
        ?.second
        ?: "A la hora de la tarea"

    Scaffold(
        modifier = Modifier,
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
            ) {
                TaskFormBottomBar(
                    isEnabled = state.isSubmitEnabled,
                    isEditMode = isEditMode,
                    onCancel = onCancel,
                    onSubmit = onSubmit
                )
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 12.dp)
                .fillMaxSize()
                .verticalScroll(scrollState)
                .testTag("taskFormContent")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isEditMode) "Editar tarea" else "Nueva tarea",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.testTag("taskFormTitle")
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = if (isEditMode)
                            "Modifica los detalles de tu tarea"
                        else
                            "Completa los detalles de tu nueva tarea",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(
                    onClick = onCancel,
                    modifier = Modifier.testTag("closeButton")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            FormSectionCard(title = "Título") {
                OutlinedTextField(
                    value = state.title,
                    onValueChange = onTitleChange,
                    shape = RoundedCornerShape(8.dp),
                    label = { Text("Título") },
                    supportingText = {
                        Text("${state.title.length}/${TaskFormLimits.TITLE_MAX}")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("titleField"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormSectionCard(title = "Descripción (opcional)") {
                OutlinedTextField(
                    value = state.description,
                    onValueChange = onDescriptionChange,
                    shape = RoundedCornerShape(8.dp),
                    label = { Text("Descripción") },
                    supportingText = {
                        Text("${state.description.length}/${TaskFormLimits.DESCRIPTION_MAX}")
                    },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("descriptionField"),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormSectionCard(title = "Nivel de importancia") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityRow(
                        selected = state.priority,
                        onPrioritySelected = onPriorityChange
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormSectionCard(title = "Fecha y hora") {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Fecha",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = onDateClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("dateButton"),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = state.date.format(dateFormatter),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Hora (opcional)",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = onTimeClick,
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("timeButton"),
                            colors = ButtonDefaults.buttonColors(
                                contentColor = MaterialTheme.colorScheme.onBackground,
                                containerColor = MaterialTheme.colorScheme.background
                            )
                        ) {
                            Text(
                                text = state.time?.format(timeFormatter) ?: "--:--",
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                if (state.dateTimeError != null) {
                    Text(
                        text = state.dateTimeError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.testTag("dateTimeError")
                    )
                }

                HorizontalDivider()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Recordatorio",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = if (state.time == null) {
                                "Selecciona una hora para activar el recordatorio"
                            } else {
                                "Recibe una notificación para esta tarea"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Switch(
                        checked = reminderEnabled,
                        onCheckedChange = { checked ->
                            onReminderChange(if (checked) 0 else null)
                        },
                        enabled = state.time != null,
                        modifier = Modifier.testTag("reminderSwitch")
                    )
                }

                if (reminderEnabled && state.time != null) {
                    ExposedDropdownMenuBox(
                        expanded = reminderMenuExpanded,
                        onExpandedChange = {
                            reminderMenuExpanded = !reminderMenuExpanded
                        }
                    ) {
                        OutlinedTextField(
                            value = selectedReminderLabel,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Avisar") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(
                                    expanded = reminderMenuExpanded
                                )
                            },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth()
                                .testTag("reminderDropdown")
                        )

                        ExposedDropdownMenu(
                            expanded = reminderMenuExpanded,
                            onDismissRequest = {
                                reminderMenuExpanded = false
                            }
                        ) {
                            reminderOptions.forEach { (minutes, label) ->
                                DropdownMenuItem(
                                    text = { Text(label) },
                                    onClick = {
                                        onReminderChange(minutes)
                                        reminderMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            FormSectionCard(title = "Ubicación (opcional)") {
                OutlinedTextField(
                    value = state.location ?: "",
                    onValueChange = onLocationChange,
                    label = { Text("Ubicación (opcional)") },
                    supportingText = {
                        val text = state.location ?: ""
                        Text("${text.length}/${TaskFormLimits.LOCATION_MAX}")
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("locationField"),
                    shape = RoundedCornerShape(8.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    )
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TaskFormBottomBar(
    isEnabled: Boolean,
    isEditMode: Boolean,
    onCancel: () -> Unit,
    onSubmit: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onCancel,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("cancelButton"),
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.primary,
                    containerColor = MaterialTheme.colorScheme.background
                )
            ) {
                Text(
                    text = "Cancelar",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Button(
                onClick = onSubmit,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("submitButton"),
                enabled = isEnabled
            ) {
                Text(
                    text = if (isEditMode) "Guardar cambios" else "Crear tarea",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

// Card contenedora de cada bloque
@Composable
private fun FormSectionCard(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        ),
        elevation = CardDefaults.elevatedCardElevation(
            defaultElevation = 1.dp
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 14.dp),
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

@Composable
private fun PriorityRow(
    selected: TaskPriority,
    onPrioritySelected: (TaskPriority) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PriorityChip(
            label = "Baja",
            selected = selected == TaskPriority.BAJA,
            onClick = { onPrioritySelected(TaskPriority.BAJA) },
            modifier = Modifier.weight(1f)
        )

        PriorityChip(
            label = "Media",
            selected = selected == TaskPriority.MEDIA,
            onClick = { onPrioritySelected(TaskPriority.MEDIA) },
            modifier = Modifier.weight(1f)
        )

        PriorityChip(
            label = "Alta",
            selected = selected == TaskPriority.ALTA,
            onClick = { onPrioritySelected(TaskPriority.ALTA) },
            modifier = Modifier.weight(1f)
        )

        PriorityChip(
            label = "Crítica",
            selected = selected == TaskPriority.CRITICA,
            onClick = { onPrioritySelected(TaskPriority.CRITICA) },
            modifier = Modifier.weight(1f)
        )
    }
}