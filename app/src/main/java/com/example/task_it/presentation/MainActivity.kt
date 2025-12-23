package com.example.task_it.presentation

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.task_it.data.preferences.ThemePreferences
import com.example.task_it.presentation.navigation.AppNavHost
import com.example.task_it.presentation.theme.TaskitTheme
import kotlinx.coroutines.launch

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
            //1) Leer tema persistido desde DataStore
            val themePrefs = ThemePreferences(this)
            val useDarkTheme by themePrefs.darkThemeFlow.collectAsState(initial = false)

            //2) Ajustar iconos del status bar según el tema
            val view = LocalView.current
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            }

            //3) Aplicar theme + navegación
            TaskitTheme(useDarkTheme = useDarkTheme) {
                AppNavHost(
                    isDarkTheme = useDarkTheme,
                    onToggleTheme = {
                        lifecycleScope.launch {
                            themePrefs.setDarkTheme(!useDarkTheme) // ✅ Guardar preferencia
                        }
                    }
                )
            }
        }
    }
}
