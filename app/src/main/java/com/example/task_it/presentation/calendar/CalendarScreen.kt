package com.example.task_it.presentation.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AssistChip
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import com.example.task_it.presentation.components.BottomTab
import com.example.task_it.presentation.components.TaskBottomBar
import com.example.task_it.presentation.components.TaskTopBar
import com.example.task_it.presentation.theme.color
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun CalendarScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onTasksClick: () -> Unit,
    onTaskClick: (Long) -> Unit
) {
    val viewModel: CalendarViewModel = viewModel()
    val state by viewModel.state.collectAsState()

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
        },

    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(Modifier.height(12.dp))

            MonthHeader(
                month = state.currentMonth,
                onPrev = viewModel::onPrevMonth,
                onNext = viewModel::onNextMonth
            )

            Spacer(Modifier.height(12.dp))

            CalendarMonthGrid(
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

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.tasksForSelectedDate.size) { i ->
                    val task = state.tasksForSelectedDate[i]
                    CalendarTaskRow(
                        task = task,
                        onClick = { onTaskClick(task.id) }
                    )
                }
            }
        }
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

    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("L", "M", "X", "J", "V", "S", "D").forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.labelMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }

    Spacer(Modifier.height(8.dp))

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
    val shape = RoundedCornerShape(12.dp)

    val containerColor =
        if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
        else MaterialTheme.colorScheme.surface

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(shape)
            .background(containerColor)
            .clickable(enabled = date != null, onClick = onClick)
            .padding(8.dp)
    ) {
        if (date != null) {
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
            )

            // ✅ Puntitos: uno por prioridad presente ese día, usando TUS colores
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp),
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
                                .background(p.color()) // 👈 aquí usa tu extensión
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
        Text(
            text = "Tareas del ${selectedDate.dayOfMonth} de $monthName",
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
private fun CalendarTaskRow(
    task: Task,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 6.dp)
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(task.title, fontWeight = FontWeight.SemiBold)

            // Esto es “placeholder” por si aún no quieres reutilizar tu TaskItem aquí
            // (lo normal es que lo sustituyas por tu item compacto con tu estilo)
            Spacer(Modifier.height(6.dp))
            val timeText = task.time?.toString() ?: "--:--"
            Text("Hora: $timeText", style = MaterialTheme.typography.bodySmall)
        }
    }
}
