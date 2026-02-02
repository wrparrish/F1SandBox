package com.parrishdev.settings.feature

import com.parrishdev.common.udf.StateProvider
import javax.inject.Inject

/**
 * Transforms [SettingsDataState] to [SettingsViewState].
 *
 * For settings, this is a straightforward 1:1 mapping since
 * there's no complex business logic to apply.
 */
class SettingsStateProvider @Inject constructor() : StateProvider<SettingsDataState, SettingsViewState> {

    override fun reduce(dataState: SettingsDataState): SettingsViewState {
        return SettingsViewState(
            notificationsEnabled = dataState.notificationsEnabled,
            darkModeEnabled = dataState.darkModeEnabled,
            showAdvancedOptions = dataState.showAdvancedOptions,
            dataLoggingEnabled = dataState.dataLoggingEnabled
        )
    }
}
