package com.example.task_it.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.task_it.presentation.calendar.CalendarContent
import com.example.task_it.presentation.components.BottomTab
import com.example.task_it.presentation.components.TaskBottomBar
import com.example.task_it.presentation.components.TaskTopBar
import com.example.task_it.presentation.tasks.list.TaskListContent
import kotlinx.coroutines.launch
import androidx.compose.animation.core.tween

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainPagerScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onAddTaskClick: () -> Unit,
    onEditTaskClick: (Long) -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { 2 })
    val scope = rememberCoroutineScope()

    val selectedTab = if (pagerState.currentPage == 0) BottomTab.TASKS else BottomTab.CALENDAR

    Scaffold(
        topBar = {
            TaskTopBar(
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize()
            ) { page ->
                when (page) {
                    0 -> TaskListContent(
                        onAddTaskClick = onAddTaskClick,
                        onEditTaskClick = onEditTaskClick
                    )
                    else -> CalendarContent(
                        onEditTaskClick = onEditTaskClick
                    )
                }
            }

            TaskBottomBar(
                selectedTab = selectedTab,
                onTasksClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = 0,
                            animationSpec = tween(durationMillis = 400)
                        )
                    }
                },
                onCalendarClick = {
                    scope.launch {
                        pagerState.animateScrollToPage(
                            page = 1,
                            animationSpec = tween(durationMillis = 400)
                        )
                    }
                },
                onAddClick = onAddTaskClick,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}