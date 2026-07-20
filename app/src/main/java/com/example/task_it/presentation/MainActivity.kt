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
import com.example.task_it.presentation.navigation.AppNavHost
import com.example.task_it.presentation.theme.TaskitTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import com.example.task_it.data.preferences.ThemePreferences
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat


class MainActivity : ComponentActivity() {

    private val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            // No necesitamos hacer nada aquí: si el usuario lo concede,
            // los próximos recordatorios podrán mostrar notificaciones.
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestNotificationPermissionIfNeeded()
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
        val themePrefs = ThemePreferences(this)
        val initialPref: Boolean? = runBlocking { themePrefs.darkThemePrefFlow.first() }


        setContent {
            val pref by themePrefs.darkThemePrefFlow.collectAsState(initial = initialPref)

            // Tema del sistema (solo se usa si pref == null)
            val systemDark = androidx.compose.foundation.isSystemInDarkTheme()

            // tema efectivo: guardado si existe, si no el del sistema
            val useDarkTheme = pref ?: systemDark

            // (tu SideEffect de status bar usando useDarkTheme)
            val view = LocalView.current
            SideEffect {
                val window = (view.context as Activity).window
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !useDarkTheme
            }

            TaskitTheme(useDarkTheme = useDarkTheme) {
                AppNavHost(
                    isDarkTheme = useDarkTheme,
                    onToggleTheme = {
                        lifecycleScope.launch {
                            // Si no había preferencia, al pulsar guardamos la contraria al tema efectivo
                            themePrefs.setDarkThemePref(!useDarkTheme)
                        }
                    }
                )
            }
        }

    }
    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

        val permission = Manifest.permission.POST_NOTIFICATIONS
        val isGranted = ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED

        if (!isGranted) {
            requestNotificationPermissionLauncher.launch(permission)
        }
    }
}
