package com.parrishdev.settings.feature

/**
 * UI-ready state for SettingsScreen.
 *
 * Contains only data needed by the UI to render.
 * NO derived properties - only constructor properties.
 */
data class SettingsViewState(
    val notificationsEnabled: Boolean,
    val darkModeEnabled: Boolean,
    val showAdvancedOptions: Boolean,
    val dataLoggingEnabled: Boolean
)
