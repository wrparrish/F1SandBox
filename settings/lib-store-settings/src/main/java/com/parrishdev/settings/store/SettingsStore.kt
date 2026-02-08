package com.parrishdev.settings.store

import kotlinx.coroutines.flow.Flow

/**
 * Store interface for app-wide user preferences.
 *
 * Follows the project's Store pattern:
 * - stream*() for reactive observation
 * - set*() for persisting preference changes
 *
 * UI layers depend on this interface; implementation is in lib-store-settings-impl.
 */
interface SettingsStore {

    /**
     * Stream the dark mode preference.
     * Emits immediately with the current persisted value, then on every change.
     *
     * @return Flow emitting true when dark mode is enabled
     */
    fun streamIsDarkMode(): Flow<Boolean>

    /**
     * Persist the dark mode preference.
     *
     * @param enabled true to enable dark mode, false for light mode
     */
    suspend fun setDarkMode(enabled: Boolean)
}
