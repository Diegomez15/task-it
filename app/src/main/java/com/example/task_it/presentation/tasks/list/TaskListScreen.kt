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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import kotlinx.coroutines.flow.distinctUntilChanged
import com.example.task_it.presentation.components.TaskTopBar
import com.example.task_it.presentation.components.TaskBottomBar
import com.example.task_it.presentation.components.BottomTab








@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit = {},
    onEditTaskClick: (Long) -> Unit,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onCalendarClick: () -> Unit
) {
    val viewModel: TaskListViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()

    var taskToDelete by remember { mutableStateOf<Task?>(null) }
    var selectedPriority by rememberSaveable { mutableStateOf<TaskPriority?>(null) } // null = Todas

    var isSearching by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }


    val tasksFiltered = remember(tasks, selectedPriority, searchQuery) {
        filterAndSortTasks(
            tasks = tasks,
            selectedPriority = selectedPriority,
            searchQuery = searchQuery
        )
    }


    val pendingTasks = tasksFiltered.filter { !it.isCompleted }
    val completedTasks = tasksFiltered.filter { it.isCompleted }


    var selectedTask by remember { mutableStateOf<Task?>(null) }

    var fabExpanded by rememberSaveable { mutableStateOf(false) }

    val listState = rememberLazyListState()

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .collect { isScrolling ->
                if (isScrolling) fabExpanded = false
            }
    }

    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        bottomBar = {
            TaskBottomBar(
                selectedTab = BottomTab.TASKS,
                onTasksClick = { /* ya estás */ },
                onCalendarClick = onCalendarClick
            )
        },
        floatingActionButton = {
            if (tasks.isNotEmpty()) {
                ExpandableTaskFab(
                    expanded = fabExpanded,
                    onToggle = { fabExpanded = !fabExpanded },
                    onSearchClick = {
                        fabExpanded = false
                        isSearching = true
                    },
                    onCreateClick = {
                        fabExpanded = false
                        onAddTaskClick()
                    }
                )
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
                // Estado vacío REAL (no hay tareas en BD)
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTaskState(onAddTaskClick = onAddTaskClick)
                }
            } else {

                // Chips siempre visibles cuando hay tareas
                TaskPriorityFilterChips(
                    selected = selectedPriority,
                    onSelectedChange = {
                        selectedPriority = it
                        fabExpanded = false
                    }
                )

                if (isSearching) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                            .padding(bottom = 12.dp),
                        shape = RoundedCornerShape(8.dp),
                        label = { Text("Buscar por título") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Filled.Search,
                                contentDescription = "Buscar"
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    searchQuery = ""
                                    isSearching = false
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Cerrar búsqueda"
                                )
                            }
                        }
                    )
                }


                if (tasksFiltered.isEmpty()) {
                    // No hay resultados para el filtro actual
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
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp),
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
                                        .clickable {
                                            fabExpanded = false
                                            selectedTask = task
                                        }

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
                                        .clickable {
                                            fabExpanded = false
                                            selectedTask = task
                                        }

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
                taskToDelete = task // usa dialog de confirmación
            },
            onEdit = {
                selectedTask = null
                onEditTaskClick(task.id) // navega al form con id
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
private fun TaskPriorityFilterChips(
    selected: TaskPriority?,
    onSelectedChange: (TaskPriority?) -> Unit

) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 0.dp),
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


