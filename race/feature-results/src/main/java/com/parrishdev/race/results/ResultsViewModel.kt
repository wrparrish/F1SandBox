package com.parrishdev.race.results

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import com.parrishdev.common.udf.BaseStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.race.store.RaceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Results screen displaying race results.
 *
 * Follows UDF pattern with lifecycle-aware store observation:
 * - [ResultsDataState]: Internal state with raw data
 * - [ResultsViewState]: UI-ready state (via [ResultsStateProvider])
 */
@HiltViewModel
class ResultsViewModel @Inject constructor(
    private val raceStore: RaceStore,
    stateProvider: ResultsStateProvider,
    viewModelBundle: ViewModelBundle,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<ResultsDataState, ResultsViewState>(
    initialDataState = ResultsDataState(
        season = savedStateHandle.get<Int>("season") ?: java.time.Year.now().value,
        round = savedStateHandle.get<Int>("round") ?: 1
    ),
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {
    private val season: Int = savedStateHandle.get<Int>("season") ?: java.time.Year.now().value
    private val round: Int = savedStateHandle.get<Int>("round") ?: 1
    init {
        // Initial data refresh (non-blocking)
        refreshResults(force = false)
    }

    /**
     * Set up lifecycle-aware store observations.
     * Collection automatically stops when UI is in background and restarts when visible.
     */
    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = raceStore.streamRaceWithResults(season, round)
        ) { raceWithResults ->
            applyMutation {
                copy(raceWithResults = raceWithResults)
                // Note: error is NOT cleared here. Errors represent network failures
                // and should only be cleared by successful refresh or explicit retry.
            }
        }
    }

    fun onRefresh() {
        refreshResults(force = true)
    }

    fun onRetry() {
        applyMutation { copy(error = null) }
        refreshResults(force = true)
    }

    private fun refreshResults(force: Boolean) {
        launchWithErrorHandling {
            applyMutation { copy(isRefreshing = force) }
            try {
                raceStore.refreshRaceResults(season, round, force)
                // Clear error on successful refresh
                applyMutation { copy(error = null) }
            } catch (e: Exception) {
                applyMutation {
                    copy(error = e.message ?: "Failed to load results")
                }
            } finally {
                applyMutation { copy(isRefreshing = false) }
            }
        }
    }
}
