package com.parrishdev.settings.feature

import com.parrishdev.common.udf.BaseEventStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 *
 * Follows UDF pattern:
 * - [SettingsDataState]: Internal state with setting values
 * - [SettingsViewState]: UI-ready state (via [SettingsStateProvider])
 * - [SettingsEvent]: One-time UI events
 *
 * Note: In a production app, settings would be persisted to DataStore.
 * For now, settings are stored in-memory only.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    stateProvider: SettingsStateProvider,
    viewModelBundle: ViewModelBundle
) : BaseEventStateViewModel<SettingsDataState, SettingsViewState, SettingsEvent>(
    initialDataState = SettingsDataState(),
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {

    fun onNotificationsToggled(enabled: Boolean) {
        applyMutation { copy(notificationsEnabled = enabled) }
        submitEvent(SettingsEvent.ShowSettingChanged("Notifications", enabled))
    }

    fun onDarkModeToggled(enabled: Boolean) {
        applyMutation { copy(darkModeEnabled = enabled) }
        submitEvent(SettingsEvent.ShowSettingChanged("Dark Mode", enabled))
    }

    fun onAdvancedOptionsToggled(enabled: Boolean) {
        applyMutation { copy(showAdvancedOptions = enabled) }
    }

    fun onDataLoggingToggled(enabled: Boolean) {
        applyMutation { copy(dataLoggingEnabled = enabled) }
        submitEvent(SettingsEvent.ShowSettingChanged("Data Logging", enabled))
    }
}
