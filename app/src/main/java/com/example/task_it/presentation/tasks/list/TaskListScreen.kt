package com.example.task_it.presentation.tasks.list

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




@Composable
fun TaskListScreen(
    modifier: Modifier = Modifier,
    onAddTaskClick: () -> Unit = {}
) {
    Scaffold(
        topBar = { TaskTopBar() },
        bottomBar = { TaskBottomBar() },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyTaskState(onAddTaskClick = onAddTaskClick)
        }
    }
    val viewModel: TaskListViewModel = viewModel()
    val tasks by viewModel.tasks.collectAsState()

    LazyColumn {
        items(tasks) { task ->
            Text(
                text = task.title,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

}

@Composable
private fun TaskTopBar() {
    Surface(
        shadowElevation = 1.dp,
        color = MaterialTheme.colorScheme.surfaceBright
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 15.dp)
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
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(top = 15.dp)
            ) {
                IconButton(onClick = { /* TODO: Cambiar tema */ }) {
                    Icon(
                        imageVector = Icons.Filled.LightMode,
                        contentDescription = "Cambiar tema"
                    )
                }
                // Aquí podrías añadir un avatar / icono de usuario si quieres
            }
        }
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
        containerColor = MaterialTheme.colorScheme.surfaceBright
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
