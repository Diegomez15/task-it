package com.example.task_it.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {

    private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")

    val darkThemeFlow: Flow<Boolean> =
        context.dataStore.data.map { prefs -> prefs[KEY_DARK_THEME] ?: false }

    suspend fun setDarkTheme(enabled: Boolean) {
        context.dataStore.edit { prefs -> prefs[KEY_DARK_THEME] = enabled }
    }
}
