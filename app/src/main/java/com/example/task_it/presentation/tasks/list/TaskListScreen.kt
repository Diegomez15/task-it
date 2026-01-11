package com.example.task_it.presentation.tasks.list

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task_it.domain.model.Task
import com.example.task_it.domain.model.TaskPriority
import com.example.task_it.presentation.components.PriorityChip
import com.example.task_it.presentation.components.TaskItem
import com.example.task_it.presentation.theme.TextSecondary
import com.example.task_it.presentation.theme.YellowPrimary
import com.example.task_it.presentation.tasks.detail.TaskDetailsBottomSheet





@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit = {},
    onEditTaskClick: (Long) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val viewModel: TaskListViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()

    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var selectedPriority by rememberSaveable { mutableStateOf<TaskPriority?>(null) } // null = Todas


    val tasksFiltered = remember(tasks, selectedPriority) {
        val base = if (selectedPriority == null) tasks else tasks.filter { it.priority == selectedPriority }
        base.sortedWith(compareBy<Task> { it.isCompleted }.thenBy { it.date })
    }

    val pendingTasks = tasksFiltered.filter { !it.isCompleted }
    val completedTasks = tasksFiltered.filter { it.isCompleted }


    var selectedTask by remember { mutableStateOf<Task?>(null) }


    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        bottomBar = { TaskBottomBar() },
        floatingActionButton = {
            // ✅ FAB solo si hay tareas (y si quieres, también solo si hay tareas filtradas)
            if (tasks.isNotEmpty()) {
                FloatingActionButton(
                    onClick = onAddTaskClick,
                    containerColor = YellowPrimary,
                    shape = RoundedCornerShape(8.dp),
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Añadir tarea")
                }
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (tasks.isEmpty()) {
                // ✅ Estado vacío REAL (no hay tareas en BD)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTaskState(onAddTaskClick = onAddTaskClick)
                }
            } else {

                // ✅ Chips siempre visibles cuando hay tareas
                TaskPriorityFilterChips(
                    selected = selectedPriority,
                    onSelectedChange = { selectedPriority = it },
                )


                if (tasksFiltered.isEmpty()) {
                    // ✅ No hay resultados para el filtro actual
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No hay tareas para este filtro",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = 12.dp // ✅ deja aire para la bottom bar + FAB
                        ),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {

                        if (pendingTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "PENDIENTES", count = pendingTasks.size)
                            }
                            items(pendingTasks, key = { it.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onToggleCompleted = { viewModel.toggleTaskCompleted(it) },
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(horizontal = 12.dp)
                                        .clickable { selectedTask = task }
                                )
                            }
                        }

                        if (completedTasks.isNotEmpty()) {
                            item {
                                SectionHeader(title = "COMPLETADAS", count = completedTasks.size)
                            }
                            items(completedTasks, key = { it.id }) { task ->
                                TaskItem(
                                    task = task,
                                    onToggleCompleted = { viewModel.toggleTaskCompleted(it) },
                                    modifier = Modifier
                                        .animateItem()
                                        .padding(horizontal = 12.dp)
                                        .clickable { selectedTask = task }
                                )
                            }
                        }

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


    // Confirmación borrar
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
            .padding(horizontal = 12.dp, vertical = 6.dp),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}


@Composable
private fun TaskTopBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    var showThemeConfirm by remember { mutableStateOf(false) }

    Surface(

        color = MaterialTheme.colorScheme.background
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(YellowPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Description,
                        contentDescription = "Logo Task-it",
                        tint = MaterialTheme.colorScheme.onPrimary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Task-it",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showThemeConfirm = true }) {
                    Icon(
                        imageVector = Icons.Filled.LightMode,
                        contentDescription = "Cambiar tema"
                    )
                }
            }
        }
    }

    if (showThemeConfirm) {
        val targetText = if (isDarkTheme) "modo claro" else "modo oscuro"

        AlertDialog(
            onDismissRequest = { showThemeConfirm = false },
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            shape = RoundedCornerShape(8.dp),
            title = { Text("Cambiar tema") },
            text = { Text("¿Quieres cambiar a $targetText?") },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showThemeConfirm = false },
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
                            onToggleTheme()
                            showThemeConfirm = false
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
                            text = "Cambiar",
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
private fun EmptyTaskState(
    onAddTaskClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = "Sin tareas",
                tint = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No hay tareas todavía",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Comienza añadiendo tu primera tarea para organizar tu día",
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary),
                textAlign = TextAlign.Center
            )
        }

        Button(
            onClick = onAddTaskClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = YellowPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Añadir tarea")
        }
    }
}

@Composable
private fun TaskBottomBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.background,


    ) {
        NavigationBarItem(
            selected = true,
            onClick = { /* Ya estamos en Tareas */ },
            icon = { Icon(imageVector = Icons.AutoMirrored.Filled.List, contentDescription = "Tareas") },
            label = { Text("Tareas") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: ir a Calendario */ },
            icon = { Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = "Calendario") },
            label = { Text("Calendario") }
        )
    }
}

@Composable
private fun TaskPriorityFilterChips(
    selected: TaskPriority?,
    onSelectedChange: (TaskPriority?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        PriorityChip(
            label = "Baja",
            selected = selected == TaskPriority.BAJA,
            modifier = Modifier.weight(1f),
            onClick = {
                onSelectedChange(if (selected == TaskPriority.BAJA) null else TaskPriority.BAJA)
            }
        )

        PriorityChip(
            label = "Media",
            selected = selected == TaskPriority.MEDIA,
            modifier = Modifier.weight(1f),
            onClick = {
                onSelectedChange(if (selected == TaskPriority.MEDIA) null else TaskPriority.MEDIA)
            }
        )

        PriorityChip(
            label = "Alta",
            selected = selected == TaskPriority.ALTA,
            modifier = Modifier.weight(1f),
            onClick = {
                onSelectedChange(if (selected == TaskPriority.ALTA) null else TaskPriority.ALTA)
            }
        )

        PriorityChip(
            label = "Crítica",
            selected = selected == TaskPriority.CRITICA,
            modifier = Modifier.weight(1f),
            onClick = {
                onSelectedChange(if (selected == TaskPriority.CRITICA) null else TaskPriority.CRITICA)
            }
        )
    }
}


