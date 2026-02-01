package com.example.task_it.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

enum class BottomTab { TASKS, CALENDAR }

@Composable
fun TaskBottomBar(
    selectedTab: BottomTab,
    onTasksClick: () -> Unit,
    onCalendarClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        NavigationBarItem(
            selected = selectedTab == BottomTab.TASKS,
            onClick = onTasksClick,
            icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = "Tareas") },
            label = { Text("Tareas") }
        )
        NavigationBarItem(
            selected = selectedTab == BottomTab.CALENDAR,
            onClick = onCalendarClick,
            icon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Calendario") },
            label = { Text("Calendario") }
        )
    }
}
