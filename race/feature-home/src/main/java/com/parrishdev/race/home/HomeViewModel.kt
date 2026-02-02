package com.parrishdev.race.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.parrishdev.common.udf.BaseEventStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.race.model.Race
import com.parrishdev.race.store.RaceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.Year
import javax.inject.Inject

/**
 * ViewModel for the Home screen displaying race list.
 *
 * Follows UDF pattern:
 * - [HomeDataState]: Internal state with raw data
 * - [HomeViewState]: UI-ready state (via [HomeStateProvider])
 * - [HomeEvent]: One-time navigation/UI events
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val raceStore: RaceStore,
    stateProvider: HomeStateProvider,
    viewModelBundle: ViewModelBundle
) : BaseEventStateViewModel<HomeDataState, HomeViewState, HomeEvent>(
    initialDataState = HomeDataState(),
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {

    private val currentSeason = Year.now().value - 1
    private var hasStartedObserving = false

    init {
        // Initial data refresh (non-blocking)
        refreshRaces(force = false)
    }

    /**
     * Set up lifecycle-aware store observations.
     * Collection automatically stops when UI is in background and restarts when visible.
     *
     * This pattern prevents wasted resources from collecting data when the user
     * can't see the updates (e.g., app minimized, screen off).
     */
    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        // Prevent multiple observation setups
        if (hasStartedObserving) return
        hasStartedObserving = true

        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = raceStore.streamRacesForSeason(currentSeason)
        ) { races ->
            applyMutation {
                copy(races = races)
                // Note: error is NOT cleared here. Errors represent network failures
                // and should only be cleared by successful refresh or explicit retry.
            }
        }
    }

    /**
     * Refresh race data from the network.
     * Called on pull-to-refresh or retry.
     */
    fun onRefresh() {
        refreshRaces(force = true)
    }

    /**
     * Retry loading after an error.
     */
    fun onRetry() {
        applyMutation { copy(error = null) }
        refreshRaces(force = true)
    }

    /**
     * User selected a race to view results.
     */
    fun onRaceSelected(race: Race) {
        submitEvent(
            HomeEvent.NavigateToRaceResults(
                season = race.season,
                round = race.round,
                raceName = race.name
            )
        )
    }

    private fun refreshRaces(force: Boolean) {
        launchWithErrorHandling {
            applyMutation { copy(isRefreshing = force) }
            try {
                raceStore.refreshRaces(currentSeason, force)
                // Clear error on successful refresh
                applyMutation { copy(error = null) }
            } catch (e: Exception) {
                applyMutation {
                    copy(error = e.message ?: "Failed to load races")
                }
                submitEvent(HomeEvent.ShowError(e.message ?: "Failed to load races"))
            } finally {
                applyMutation { copy(isRefreshing = false) }
            }
        }
    }
}
