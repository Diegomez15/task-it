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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.ui.graphics.Brush
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


@Composable
fun CalendarScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onTasksClick: () -> Unit,
    onEditTaskClick: (Long) -> Unit,
    onAddTaskClick: () -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    val pendingTasks = remember(state.tasksForSelectedDate) {
        state.tasksForSelectedDate.filter { !it.isCompleted }
    }

    val completedTasks = remember(state.tasksForSelectedDate) {
        state.tasksForSelectedDate.filter { it.isCompleted }
    }

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    fun handleToggleCompleted(task: Task) {
        val wasCompleted = task.isCompleted
        viewModel.toggleCompleted(task)

        if (!wasCompleted) {
            scope.launch {
                val result = snackbarHostState.showSnackbar(
                    message = "Tarea completada",
                    actionLabel = "Deshacer",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    viewModel.toggleCompleted(task.copy(isCompleted = true))
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
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
                    onSelect = viewModel::onSelectDate,
                    onPrevMonth = viewModel::onPrevMonth,
                    onNextMonth = viewModel::onNextMonth
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
                        contentPadding = PaddingValues(bottom = 110.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {

                        if (pendingTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "PENDIENTES", count = pendingTasks.size)
                            }
                            items(pendingTasks, key = { it.id }) { task ->
                                CalendarTaskRow(
                                    task = task,
                                    onClick = { selectedTask = task },
                                    onToggleCompleted = { handleToggleCompleted(it) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }

                        if (completedTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "COMPLETADAS", count = completedTasks.size)
                            }
                            items(completedTasks, key = { it.id }) { task ->
                                CalendarTaskRow(
                                    task = task,
                                    onClick = { selectedTask = task },
                                    onToggleCompleted = { handleToggleCompleted(it) },
                                    modifier = Modifier.animateItem()
                                )
                            }
                        }
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.background.copy(alpha = 0f),
                                MaterialTheme.colorScheme.background
                            )
                        )
                    )
            )

            TaskBottomBar(
                selectedTab = BottomTab.CALENDAR,
                onTasksClick = onTasksClick,
                onCalendarClick = { /* ya estás */ },
                onAddClick = onAddTaskClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp),
                snackbar = { data -> TaskSnackbar(data) }
            )
        }
    }

    selectedTask?.let { task ->
        TaskDetailsBottomSheet(
            task = task,
            onDismiss = { selectedTask = null },
            onDelete = {
                selectedTask = null
                taskToDelete = task
            },
            onEdit = {
                selectedTask = null
                onEditTaskClick(task.id)
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
private fun SectionHeader(title: String, count: Int) {
    Text(
        text = "$title ($count)",
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
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
    onSelect: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
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
                onSelect = onSelect,
                onPrevMonth = onPrevMonth,
                onNextMonth = onNextMonth
            )
        }
    }
}

private data class CalendarCell(
    val date: LocalDate,
    val inCurrentMonth: Boolean
)

@Composable
private fun CalendarMonthGrid(
    month: YearMonth,
    selectedDate: LocalDate,
    markers: Map<LocalDate, List<TaskPriority>>,
    onSelect: (LocalDate) -> Unit,
    onPrevMonth: () -> Unit,
    onNextMonth: () -> Unit
) {
    val firstDayOfMonth = month.atDay(1)
    val daysInMonth = month.lengthOfMonth()

    // Lunes como primer día (L=0 ... D=6)
    val firstDowIndex = ((firstDayOfMonth.dayOfWeek.value + 6) % 7)

    val prevMonth = month.minusMonths(1)
    val nextMonth = month.plusMonths(1)
    val prevMonthDays = prevMonth.lengthOfMonth()

    // Calculamos cuántas filas necesitamos (de 4 a 6 normalmente)
    val totalCells = firstDowIndex + daysInMonth
    val rows = (totalCells + 6) / 7
    val totalGridCells = rows * 7

    val cells = buildList {
        // Días del mes anterior (relleno al inicio)
        val startPrevDay = prevMonthDays - firstDowIndex + 1
        for (d in startPrevDay..prevMonthDays) {
            add(CalendarCell(prevMonth.atDay(d), inCurrentMonth = false))
        }

        // Días del mes actual
        for (d in 1..daysInMonth) {
            add(CalendarCell(month.atDay(d), inCurrentMonth = true))
        }

        // Días del mes siguiente (relleno al final)
        val remaining = totalGridCells - size
        for (d in 1..remaining) {
            add(CalendarCell(nextMonth.atDay(d), inCurrentMonth = false))
        }
    }

    // Header de días semana
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listOf("L", "M", "X", "J", "V", "S", "D").forEach { label ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 10.dp),
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

    for (r in 0 until rows) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            for (c in 0..6) {
                val i = r * 7 + c
                val cell = cells[i]

                DayCell(
                    date = cell.date,
                    inCurrentMonth = cell.inCurrentMonth,
                    selected = cell.date == selectedDate,
                    markers = markers[cell.date].orEmpty(),
                    onClick = {
                        // Si pulsas un día fuera del mes, cambia de mes automáticamente
                        if (!cell.inCurrentMonth) {
                            if (cell.date.isBefore(firstDayOfMonth)) onPrevMonth() else onNextMonth()
                        }
                        onSelect(cell.date)
                    },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    inCurrentMonth: Boolean,
    selected: Boolean,
    markers: List<TaskPriority>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val shape = RoundedCornerShape(8.dp)

    val containerColor =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)
        else MaterialTheme.colorScheme.surfaceBright

    val contentAlpha = if (inCurrentMonth) 1f else 0.35f

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(vertical = 1.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = date.dayOfMonth.toString(),
            modifier = Modifier.alpha(contentAlpha),
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            textAlign = TextAlign.Center
        )

        // Puntitos de importancia
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
                .alpha(contentAlpha),
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
private fun TaskSnackbar(data: SnackbarData) {
    Snackbar(
        snackbarData = data,
        modifier = Modifier.padding(horizontal = 12.dp),
        shape = RoundedCornerShape(8.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onBackground,
        actionColor = YellowPrimary
    )
}

@Composable
private fun CalendarTaskRow(
    task: Task,
    onClick: () -> Unit,
    onToggleCompleted: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    val contentAlpha = if (task.isCompleted) 0.45f else 1f
    val stripeAlpha = if (task.isCompleted) 0.45f else 1f
    val barColor = task.priority.color()

    ElevatedCard(
        modifier = modifier
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
                .height(IntrinsicSize.Min)
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Barra decorativa izquierda (con alpha si completada)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(8.dp))
                    .alpha(stripeAlpha)
                    .background(barColor)
            )

            Spacer(Modifier.width(12.dp))

            Column(
                modifier = Modifier
                    .weight(1f)
                    .alpha(contentAlpha),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleSmall.copy(
                        textDecoration = if (task.isCompleted) TextDecoration.LineThrough else null
                    )
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
    }
}

private fun formatTime(time: LocalTime): String =
    "%02d:%02d".format(time.hour, time.minute)