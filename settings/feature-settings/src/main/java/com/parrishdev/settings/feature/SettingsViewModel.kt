package com.parrishdev.settings.feature

import androidx.lifecycle.LifecycleOwner
import com.parrishdev.common.udf.BaseEventStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.settings.store.SettingsStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 *
 * Follows UDF pattern:
 * - [SettingsDataState]: Internal state with setting values
 * - [SettingsViewState]: UI-ready state (via [SettingsStateProvider])
 * - [SettingsEvent]: One-time UI events (e.g., snackbar confirmations)
 *
 * Dark mode is persisted via [SettingsStore] (DataStore).
 * Other settings remain in-memory for now.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    stateProvider: SettingsStateProvider,
    viewModelBundle: ViewModelBundle,
    private val settingsStore: SettingsStore
) : BaseEventStateViewModel<SettingsDataState, SettingsViewState, SettingsEvent>(
    initialDataState = SettingsDataState(),
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {

    /**
     * Set up lifecycle-aware store observations.
     * Collection automatically stops when UI is in background and restarts when visible.
     *
     * Note: No guard needed here — when the lifecycle owner changes (e.g., screen rotation),
     * the old coroutine's repeatOnLifecycle completes naturally (lifecycle reaches DESTROYED),
     * so a new call safely starts a fresh observation with the new lifecycle owner.
     */
    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = settingsStore.streamIsDarkMode()
        ) { isDarkMode ->
            applyMutation { copy(darkModeEnabled = isDarkMode) }
        }
    }

    fun onNotificationsToggled(enabled: Boolean) {
        applyMutation { copy(notificationsEnabled = enabled) }
        submitEvent(SettingsEvent.ShowSettingChanged("Notifications", enabled))
    }

    fun onDarkModeToggled(enabled: Boolean) {
        launchWithErrorHandling {
            settingsStore.setDarkMode(enabled)
        }
    }

    fun onAdvancedOptionsToggled(enabled: Boolean) {
        applyMutation { copy(showAdvancedOptions = enabled) }
    }

    fun onDataLoggingToggled(enabled: Boolean) {
        applyMutation { copy(dataLoggingEnabled = enabled) }
        submitEvent(SettingsEvent.ShowSettingChanged("Data Logging", enabled))
    }
}
