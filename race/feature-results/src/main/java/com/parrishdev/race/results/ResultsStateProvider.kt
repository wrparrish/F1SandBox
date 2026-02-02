package com.parrishdev.race.results

import com.parrishdev.common.udf.StateProvider
import javax.inject.Inject

/**
 * Transforms [ResultsDataState] to [ResultsViewState].
 */
class ResultsStateProvider @Inject constructor() : StateProvider<ResultsDataState, ResultsViewState> {

    override fun reduce(dataState: ResultsDataState): ResultsViewState {
        val raceWithResults = dataState.raceWithResults
        return ResultsViewState(
            isLoading = dataState.isLoading,
            isRefreshing = dataState.isRefreshing,
            raceName = raceWithResults?.race?.name ?: "Loading...",
            circuitName = raceWithResults?.race?.circuit?.name ?: "",
            date = raceWithResults?.race?.date ?: "",
            results = raceWithResults?.results ?: emptyList(),
            errorMessage = dataState.error
        )
    }
}
