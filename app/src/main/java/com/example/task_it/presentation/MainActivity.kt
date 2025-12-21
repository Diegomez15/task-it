package com.example.task_it.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.task_it.presentation.navigation.AppNavHost
import com.example.task_it.presentation.theme.TaskitTheme
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.auto(
                lightScrim = android.graphics.Color.TRANSPARENT,
                darkScrim = android.graphics.Color.TRANSPARENT
            )
        )
        setContent {
            TaskitTheme {
                AppNavHost()   // Por ahora solo mostrará la HomeScreen
            }
        }
    }
}
