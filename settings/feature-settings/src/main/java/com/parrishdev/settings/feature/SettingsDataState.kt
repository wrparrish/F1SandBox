package com.parrishdev.settings.feature

/**
 * Internal data state for [SettingsViewModel].
 *
 * [darkModeEnabled] is loaded from [SettingsStore] and persisted to DataStore.
 * Other settings remain in-memory only for now.
 */
data class SettingsDataState(
    val notificationsEnabled: Boolean = true,
    val darkModeEnabled: Boolean = true,
    val showAdvancedOptions: Boolean = false,
    val dataLoggingEnabled: Boolean = true
)
