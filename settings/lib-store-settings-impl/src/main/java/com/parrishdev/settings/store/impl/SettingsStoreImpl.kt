package com.parrishdev.settings.store.impl

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.parrishdev.settings.store.SettingsStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    name = "f1_sandbox_settings"
)

/**
 * DataStore-backed implementation of [SettingsStore].
 *
 * Persists user preferences to disk using Jetpack DataStore Preferences.
 * Default: dark mode enabled (matching the app's "Night Race" design language).
 */
@Singleton
class SettingsStoreImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsStore {

    override fun streamIsDarkMode(): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[DARK_MODE_KEY] ?: DEFAULT_DARK_MODE
            }
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        private const val DEFAULT_DARK_MODE = true
    }
}
