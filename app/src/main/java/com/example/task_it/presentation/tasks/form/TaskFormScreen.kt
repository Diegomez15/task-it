package com.example.task_it.presentation.tasks.form

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.task_it.presentation.theme.TextSecondary
import com.example.task_it.presentation.theme.YellowPrimary
import java.time.LocalDate
import java.time.LocalTime

@Composable
fun TaskFormScreen(
    viewModel: TaskFormViewModel = TaskFormViewModel(),
    onCancel: () -> Unit = {},
    onCreateTask: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp)
        ) {

            // ---------- HEADER ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Nueva tarea",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Completa los detalles de tu nueva tarea",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                }

                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.Close, contentDescription = "Cerrar")
                }
            }

            Spacer(Modifier.height(30.dp))

            // ---------- CAMPO TÍTULO ----------
            CardInputSection(title = "Título") {
                OutlinedTextField(
                    value = uiState.title,
                    onValueChange = viewModel::onTitleChange,
                    placeholder = { Text("Escribe el título de la tarea") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = YellowPrimary,
                        cursorColor = YellowPrimary
                    )
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- PRIORIDAD ----------
            CardInputSection(title = "Nivel de importancia") {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    PriorityOption("Crítica", TaskPriority.CRITICA, uiState, viewModel)
                    PriorityOption("Alta", TaskPriority.ALTA, uiState, viewModel)
                    PriorityOption("Media", TaskPriority.MEDIA, uiState, viewModel)
                    PriorityOption("Baja", TaskPriority.BAJA, uiState, viewModel)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- FECHA Y HORA ----------
            CardInputSection(title = "Fecha y hora") {

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = uiState.date?.toString() ?: "",
                        onValueChange = {},
                        placeholder = { Text("mm / dd / yyyy") },
                        modifier = Modifier.weight(1f),
                        enabled = false
                    )

                    OutlinedTextField(
                        value = uiState.time?.toString() ?: "",
                        onValueChange = {},
                        placeholder = { Text("--:--") },
                        modifier = Modifier.weight(1f),
                        enabled = false
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // ---------- UBICACIÓN ----------
            CardInputSection(title = "Ubicación (opcional)") {
                OutlinedTextField(
                    value = uiState.location,
                    onValueChange = viewModel::onLocationChange,
                    placeholder = { Text("Ej: Oficina, Casa, Reunión online") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                )
            }

            Spacer(Modifier.height(20.dp))

            // ---------- BOTONES ----------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = onCancel,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(45.dp).width(150.dp),
                ) {
                    Text("Cancelar")
                }

                Button(
                    onClick = onCreateTask,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(45.dp).width(150.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowPrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Crear tarea")
                }
            }
        }
    }
}

@Composable
fun PriorityOption(
    label: String,
    priority: TaskPriority,
    uiState: TaskFormUiState,
    viewModel: TaskFormViewModel
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = uiState.priority == priority,
            onClick = { viewModel.onPriorityChange(priority) },
            colors = RadioButtonDefaults.colors(
                selectedColor = YellowPrimary
            )
        )
        Text(label, fontSize = 16.sp)
    }
}

@Composable
fun CardInputSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp
        )
        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceBright)
                    .padding(16.dp),
                content = content
            )
        }
    }
}
