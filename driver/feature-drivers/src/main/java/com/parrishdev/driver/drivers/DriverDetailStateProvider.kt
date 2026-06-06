package com.parrishdev.driver.drivers

import com.parrishdev.common.udf.StateProvider
import javax.inject.Inject

/**
 * Transforms [DriverDetailDataState] to [DriverDetailViewState].
 *
 * Converts internal business state to clean, UI-ready view state.
 */
class DriverDetailStateProvider @Inject constructor() : StateProvider<DriverDetailDataState, DriverDetailViewState> {

    override fun reduce(dataState: DriverDetailDataState): DriverDetailViewState {
        return DriverDetailViewState(
            isLoading = dataState.isLoading,
            isRefreshing = dataState.isRefreshing,
            driver = dataState.driver,
            errorMessage = dataState.error
        )
    }
}
