package com.example.task_it.presentation.tasks.list

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.task_it.presentation.theme.TextSecondary
import com.example.task_it.presentation.theme.YellowPrimary
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.example.task_it.domain.model.Task
import com.example.task_it.presentation.components.TaskItem


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit = {},
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
)
{
    val viewModel: TaskListViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()
    var taskToDelete by remember { mutableStateOf<Task?>(null) }


    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        bottomBar = { TaskBottomBar() },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddTaskClick,
                containerColor = YellowPrimary
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Añadir tarea")
            }
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->

        // ✅ Todo el contenido de la pantalla va aquí dentro
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            if (tasks.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    EmptyTaskState(onAddTaskClick = onAddTaskClick)
                }
            } else {
                // Aquí luego metemos tus chips de filtro, ahora solo lista bien colocada:
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(tasks, key = { it.id }) { task ->
                        TaskItem(
                            task = task,
                            onEdit = { /* TODO */ },
                            onDelete = { taskToDelete = it },
                            modifier = Modifier
                                .animateItemPlacement()
                                .padding(horizontal = 16.dp)
                        )
                    }

                }
            }
        }
    }
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            containerColor = MaterialTheme.colorScheme.surfaceBright,
            title = { Text("Eliminar tarea") },
            text = { Text("¿Seguro que quieres eliminar “${taskToDelete!!.title}”?") },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = { taskToDelete = null },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(40.dp)
                            .widthIn(min = 120.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Button(
                        onClick = {
                            viewModel.deleteTask(taskToDelete!!)
                            taskToDelete = null
                        },
                        shape = RoundedCornerShape(50),
                        modifier = Modifier
                            .height(40.dp)
                            .widthIn(min = 120.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = YellowPrimary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text("Eliminar")
                    }
                }
            }

        )
    }
}


@Composable
private fun TaskTopBar(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit

) {
    var showThemeConfirm by remember { mutableStateOf(false) }
    Surface(
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceBright
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars) // ✅ separa del status bar
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                //modifier = Modifier.offset(y = (-13).dp), // 👈 mueve todo hacia arriba
                verticalAlignment = Alignment.CenterVertically
            ) {
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
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Task-it",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold
                    )
                )
            }

            Row(
                //modifier = Modifier.offset(y = (-13).dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
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
            title = { Text("Cambiar tema") },
            text = { Text("¿Quieres cambiar a $targetText?") },
            dismissButton = {
                OutlinedButton(
                    onClick = { showThemeConfirm = false },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(40.dp)
                        .widthIn(min = 120.dp)
                ) { Text("Cancelar") }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showThemeConfirm = false
                        onToggleTheme()
                    },
                    shape = RoundedCornerShape(50),
                    modifier = Modifier
                        .height(40.dp)
                        .widthIn(min = 120.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = YellowPrimary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) { Text("Cambiar") }
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
        // Icono circular grande en el centro
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Filled.List,
                contentDescription = "Sin tareas",
                tint = TextSecondary
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No hay tareas todavía",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Comienza añadiendo tu primera tarea para organizar tu día",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondary
                )
            )
        }

        Button(
            onClick = onAddTaskClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = YellowPrimary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Añadir tarea")
        }
    }
}



@Composable
private fun TaskBottomBar() {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surfaceBright,
        modifier = Modifier.shadow(
            elevation = 10.dp
        )
    ){
        NavigationBarItem(
            selected = true,
            onClick = { /* Ya estamos en Tareas */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.List,
                    contentDescription = "Tareas"
                )
            },
            label = { Text("Tareas") }
        )
        NavigationBarItem(
            selected = false,
            onClick = { /* TODO: ir a Calendario */ },
            icon = {
                Icon(
                    imageVector = Icons.Filled.CalendarToday,
                    contentDescription = "Calendario"
                )
            },
            label = { Text("Calendario") }
        )
    }
}
