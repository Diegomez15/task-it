package com.example.task_it.presentation.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.task_it.presentation.theme.YellowPrimary

enum class BottomTab { TASKS, CALENDAR }

private val BarHeight = 64.dp
private val CenterButtonSize = 44.dp
private val IndicatorInset = 6.dp

@Composable
fun TaskBottomBar(
    selectedTab: BottomTab,
    onTasksClick: () -> Unit,
    onCalendarClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 24.dp, vertical = 0.dp)
            .height(BarHeight),
        shape = RoundedCornerShape(32.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 6.dp
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val itemWidth = (maxWidth - CenterButtonSize) / 2
            val indicatorWidth = itemWidth - IndicatorInset * 2

            val targetOffsetX = if (selectedTab == BottomTab.TASKS) {
                IndicatorInset
            } else {
                itemWidth + CenterButtonSize + IndicatorInset
            }

            val animatedOffsetX by animateDpAsState(
                targetValue = targetOffsetX,
                animationSpec = tween(durationMillis = 300),
                label = "bottomBarIndicatorOffset"
            )

            // Indicador deslizante (pastilla de fondo)
            Box(
                modifier = Modifier
                    .offset(x = animatedOffsetX)
                    .padding(vertical = IndicatorInset)
                    .width(indicatorWidth)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(32.dp))
                    .background(MaterialTheme.colorScheme.surfaceBright)
            )

            Row(modifier = Modifier.fillMaxSize()) {
                BottomBarItem(
                    icon = Icons.AutoMirrored.Filled.List,
                    label = "Tareas",
                    selected = selectedTab == BottomTab.TASKS,
                    onClick = onTasksClick,
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                )

                Box(
                    modifier = Modifier
                        .width(CenterButtonSize)
                        .fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(CenterButtonSize)
                            .clip(CircleShape)
                            .background(YellowPrimary)
                            .clickable(onClick = onAddClick),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Nueva tarea",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }

                BottomBarItem(
                    icon = Icons.Filled.CalendarToday,
                    label = "Calendario",
                    selected = selectedTab == BottomTab.CALENDAR,
                    onClick = onCalendarClick,
                    modifier = Modifier
                        .width(itemWidth)
                        .fillMaxHeight()
                )
            }
        }
    }
}

@Composable
private fun BottomBarItem(
    icon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val contentColor =
        if (selected) YellowPrimary else MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .padding(IndicatorInset)
            .clip(RoundedCornerShape(32.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = contentColor)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = contentColor
        )
    }
}