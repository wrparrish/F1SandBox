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
        return streamPreference(DARK_MODE_KEY, DEFAULT_DARK_MODE)
    }

    override suspend fun setDarkMode(enabled: Boolean) {
        setPreference(DARK_MODE_KEY, enabled)
    }

    override fun streamIsNotificationsEnabled(): Flow<Boolean> {
        return streamPreference(NOTIFICATIONS_KEY, DEFAULT_NOTIFICATIONS)
    }

    override suspend fun setNotificationsEnabled(enabled: Boolean) {
        setPreference(NOTIFICATIONS_KEY, enabled)
    }

    override fun streamShowAdvancedOptions(): Flow<Boolean> {
        return streamPreference(SHOW_ADVANCED_KEY, DEFAULT_SHOW_ADVANCED)
    }

    override suspend fun setAdvancedOptions(enabled: Boolean) {
        setPreference(SHOW_ADVANCED_KEY, enabled)
    }

    override fun streamIsDataLoggingEnabled(): Flow<Boolean> {
        return streamPreference(DATA_LOGGING_KEY, DEFAULT_DATA_LOGGING)
    }

    override suspend fun setDataLoggingEnabled(enabled: Boolean) {
        setPreference(DATA_LOGGING_KEY, enabled)
    }

    private fun streamPreference(key: Preferences.Key<Boolean>, defaultValue: Boolean): Flow<Boolean> {
        return context.dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    emit(androidx.datastore.preferences.core.emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { preferences ->
                preferences[key] ?: defaultValue
            }
    }

    private suspend fun setPreference(key: Preferences.Key<Boolean>, value: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode_enabled")
        private val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        private val SHOW_ADVANCED_KEY = booleanPreferencesKey("show_advanced_options")
        private val DATA_LOGGING_KEY = booleanPreferencesKey("data_logging_enabled")

        private const val DEFAULT_DARK_MODE = true
        private const val DEFAULT_NOTIFICATIONS = true
        private const val DEFAULT_SHOW_ADVANCED = false
        private const val DEFAULT_DATA_LOGGING = true
    }
}
