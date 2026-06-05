package com.parrishdev.race.home

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.viewModelScope
import com.parrishdev.common.udf.BaseEventStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.race.model.Race
import com.parrishdev.race.store.RaceStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flatMapLatest
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
    private val selectedSeasonFlow = MutableStateFlow(currentSeason)

    init {
        // Initial data refresh (non-blocking)
        refreshRaces(season = currentSeason, force = false)
    }

    /**
     * Set up lifecycle-aware store observations.
     * Collection automatically stops when UI is in background and restarts when visible.
     */
    @OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = selectedSeasonFlow.flatMapLatest { season ->
                raceStore.streamRacesForSeason(season)
            }
        ) { races ->
            applyMutation {
                copy(races = races)
            }
        }
    }

    /**
     * Called when the active F1 season is changed.
     */
    fun onSeasonChanged(season: Int) {
        selectedSeasonFlow.value = season
        refreshRaces(season = season, force = false)
    }

    /**
     * Refresh race data from the network.
     * Called on pull-to-refresh or retry.
     */
    fun onRefresh() {
        refreshRaces(season = selectedSeasonFlow.value, force = true)
    }

    /**
     * Retry loading after an error.
     */
    fun onRetry() {
        applyMutation { copy(error = null) }
        refreshRaces(season = selectedSeasonFlow.value, force = true)
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

    private fun refreshRaces(season: Int, force: Boolean) {
        launchWithErrorHandling {
            applyMutation { copy(isRefreshing = force) }
            try {
                raceStore.refreshRaces(season, force)
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
