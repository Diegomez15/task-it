package com.example.task_it.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.RadioButtonUnchecked
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import com.example.task_it.presentation.components.BottomTab
import com.example.task_it.presentation.components.TaskBottomBar
import com.example.task_it.presentation.components.TaskTopBar
import com.example.task_it.presentation.theme.color
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.task_it.presentation.tasks.detail.TaskDetailsBottomSheet
import com.example.task_it.presentation.theme.YellowPrimary


@Composable
fun CalendarScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onTasksClick: () -> Unit,
    onEditTaskClick: (Long) -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }


    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        bottomBar = {
            TaskBottomBar(
                selectedTab = BottomTab.CALENDAR,
                onTasksClick = onTasksClick,
                onCalendarClick = { /* ya estás */ }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 12.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            MonthHeader(
                month = state.currentMonth,
                onPrev = viewModel::onPrevMonth,
                onNext = viewModel::onNextMonth
            )

            Spacer(Modifier.height(12.dp))

            CalendarCard(
                month = state.currentMonth,
                selectedDate = state.selectedDate,
                markers = state.dayMarkers,
                onSelect = viewModel::onSelectDate
            )

            Spacer(Modifier.height(16.dp))

            TasksSectionHeader(
                selectedDate = state.selectedDate,
                taskCount = state.tasksForSelectedDate.size
            )

            Spacer(Modifier.height(10.dp))

            if (state.tasksForSelectedDate.isEmpty()) {
                EmptyDayCard(
                    isToday = state.selectedDate == LocalDate.now()
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.tasksForSelectedDate) { task ->
                        CalendarTaskRow(
                            task = task,
                            onClick = { selectedTask = task }
                        )
                    }
                }
            }
        }
    }
    selectedTask?.let { task ->
        TaskDetailsBottomSheet(
            task = task,
            onDismiss = { selectedTask = null },
            onDelete = {
                selectedTask = null
                taskToDelete = task // ✅ usa tu dialog de confirmación actual
            },
            onEdit = {
                selectedTask = null
                onEditTaskClick(task.id) // ✅ navega al form con id
            }
        )
    }
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(8.dp),
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que quieres eliminar “${taskToDelete!!.title}”?") },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { taskToDelete = null },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text(
                            text = "Cancelar",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Button(
                        onClick = {
                            viewModel.deleteTask(taskToDelete!!)
                            taskToDelete = null
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowPrimary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = "Eliminar",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

            }
        )
    }

}

@Composable
private fun MonthHeader(
    month: YearMonth,
    onPrev: () -> Unit,
    onNext: () -> Unit
) {
    val label = remember(month) {
        val m = month.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        "$m ${month.year}"
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onPrev) {
            Icon(Icons.Default.ChevronLeft, contentDescription = "Mes anterior")
        }
        IconButton(onClick = onNext) {
            Icon(Icons.Default.ChevronRight, contentDescription = "Mes siguiente")
        }
    }
}

@Composable
private fun CalendarCard(
    month: YearMonth,
    selectedDate: LocalDate,
    markers: Map<LocalDate, List<TaskPriority>>,
    onSelect: (LocalDate) -> Unit
) {

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Column(
            modifier = Modifier
                .padding(12.dp)
        ) {
            CalendarMonthGrid(
                month = month,
                selectedDate = selectedDate,
                markers = markers,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    markers: Map<LocalDate, List<TaskPriority>>,
    onSelect: (LocalDate) -> Unit
) {
    val firstDayOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    // Lunes como primer día (L=0 ... D=6)
    val firstDowIndex = ((firstDayOfMonth.dayOfWeek.value + 6) % 7)

    val days = buildList<LocalDate?> {
        repeat(firstDowIndex) { add(null) }
        for (d in 1..daysInMonth) add(month.atDay(d))
    }

    // ✅ IMPORTANTE: misma distribución que las filas del mes
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("L", "M", "X", "J", "V", "S", "D").forEach { label ->
            Box(
                modifier = Modifier.weight(1f).padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
            }
        }
    }

    Spacer(Modifier.height(10.dp))

    val rows = (days.size + 6) / 7
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        for (r in 0 until rows) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                for (c in 0..6) {
                    val i = r * 7 + c
                    val date = days.getOrNull(i)
                    DayCell(
                        date = date,
                        selected = date == selectedDate,
                        markers = date?.let { markers[it].orEmpty() }.orEmpty(),
                        onClick = { if (date != null) onSelect(date) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate?,
    selected: Boolean,
    markers: List<TaskPriority>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)

    val containerColor =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.surfaceBright

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(containerColor)
            .clickable(enabled = date != null, onClick = onClick)
            .padding(vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            // ✅ número centrado como en tu referencia
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                textAlign = TextAlign.Center
            )

            // ✅ Puntitos de prioridad (tus colores)
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val order = listOf(
                    TaskPriority.BAJA,
                    TaskPriority.MEDIA,
                    TaskPriority.ALTA,
                    TaskPriority.CRITICA
                )

                order
                    .filter { it in markers }
                    .take(4)
                    .forEach { p ->
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(RoundedCornerShape(50))
                                .background(p.color())
                        )
                    }
            }
        }
    }
}

@Composable
private fun TasksSectionHeader(selectedDate: LocalDate, taskCount: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val monthName = selectedDate.month.getDisplayName(TextStyle.FULL, Locale("es", "ES"))
        val title = if (selectedDate == LocalDate.now()) {
            "Tareas de hoy"
        } else {
            "Tareas del ${selectedDate.dayOfMonth} de $monthName"
        }

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f)
        )

        AssistChip(
            onClick = {},
            label = { Text("$taskCount tarea${if (taskCount == 1) "" else "s"}") },
            enabled = false
        )
    }
}

@Composable
private fun EmptyDayCard(isToday: Boolean) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Text(
            text = if (isToday) "No hay tareas programadas para este día" else "No hay tareas programadas para este día",
            modifier = Modifier.padding(14.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun CalendarTaskRow(
    task: Task,
    onClick: () -> Unit
) {
    val barColor = task.priority.color()

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.surfaceBright
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barrita izquierda
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(barColor)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val timeText = task.time?.let { formatTime(it) }
                    if (timeText != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Schedule,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = timeText,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1
                            )
                        }
                    }

                    val loc = task.location?.takeIf { it.isNotBlank() }
                    if (loc != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                }
            }

            // “checkbox” a la derecha (visual)
            Icon(
                imageVector = Icons.Outlined.RadioButtonUnchecked,
                contentDescription = "Completar",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatTime(time: LocalTime): String =
    "%02d:%02d".format(time.hour, time.minute)
