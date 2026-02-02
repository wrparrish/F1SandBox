package com.parrishdev.settings.feature

/**
 * One-time events emitted by [SettingsViewModel].
 * These are consumed by the UI for navigation, toasts, etc.
 */
sealed interface SettingsEvent {

    /**
     * Show a message when a setting is changed.
     */
    data class ShowSettingChanged(val settingName: String, val enabled: Boolean) : SettingsEvent
}
