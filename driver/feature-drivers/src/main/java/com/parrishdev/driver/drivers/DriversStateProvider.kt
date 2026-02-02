package com.parrishdev.driver.drivers

import com.parrishdev.common.udf.StateProvider
import javax.inject.Inject

/**
 * Transforms [DriversDataState] to [DriversViewState].
 *
 * This is where business logic state is converted to UI-ready state.
 */
class DriversStateProvider @Inject constructor() : StateProvider<DriversDataState, DriversViewState> {

    override fun reduce(dataState: DriversDataState): DriversViewState {
        return DriversViewState(
            isLoading = dataState.isLoading,
            isRefreshing = dataState.isRefreshing,
            drivers = dataState.drivers ?: emptyList(),
            errorMessage = dataState.error,
            showEmptyState = !dataState.isLoading && !dataState.hasData && dataState.error == null
        )
    }
}
