package com.parrishdev.race.home

import com.parrishdev.common.udf.StateProvider
import javax.inject.Inject

/**
 * Transforms [HomeDataState] to [HomeViewState].
 *
 * This is where business logic state is converted to UI-ready state.
 */
class HomeStateProvider @Inject constructor() : StateProvider<HomeDataState, HomeViewState> {

    override fun reduce(dataState: HomeDataState): HomeViewState {
        return HomeViewState(
            isLoading = dataState.isLoading,
            isRefreshing = dataState.isRefreshing,
            races = dataState.races ?: emptyList(),
            errorMessage = dataState.error,
            showEmptyState = !dataState.isLoading && !dataState.hasData && dataState.error == null
        )
    }
}
