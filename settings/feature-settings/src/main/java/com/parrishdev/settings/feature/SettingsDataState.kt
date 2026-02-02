package com.parrishdev.settings.feature

/**
 * Internal data state for [SettingsViewModel].
 *
 * Contains the current state of all settings toggles.
 * In a production app, these would be persisted to DataStore/SharedPreferences.
 */
data class SettingsDataState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = false,
    val showAdvancedOptions: Boolean = false,
    val dataLoggingEnabled: Boolean = true
)
