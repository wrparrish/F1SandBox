package com.parrishdev.driver.drivers

import androidx.lifecycle.LifecycleOwner
import com.parrishdev.common.udf.BaseEventStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.driver.model.Driver
import com.parrishdev.driver.store.DriverStore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel for the Drivers screen displaying driver list.
 *
 * Follows UDF pattern with lifecycle-aware store observation:
 * - [DriversDataState]: Internal state with raw data
 * - [DriversViewState]: UI-ready state (via [DriversStateProvider])
 * - [DriversEvent]: One-time navigation/UI events
 */
@HiltViewModel
class DriversViewModel @Inject constructor(
    private val driverStore: DriverStore,
    stateProvider: DriversStateProvider,
    viewModelBundle: ViewModelBundle
) : BaseEventStateViewModel<DriversDataState, DriversViewState, DriversEvent>(
    initialDataState = DriversDataState(),
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {
    private var hasStartedObserving = false

    init {
        // Always force refresh on init to ensure we have current season drivers
        refreshDrivers(force = true)
    }

    /**
     * Set up lifecycle-aware store observations.
     * Collection automatically stops when UI is in background and restarts when visible.
     */
    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        if (hasStartedObserving) return
        hasStartedObserving = true

        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = driverStore.streamAllDrivers()
                .map { drivers -> drivers.sortedByDescending { it.championshipPoints } }
        ) { drivers ->
            applyMutation {
                copy(drivers = drivers)
                // Note: error is NOT cleared here. Errors represent network failures
                // and should only be cleared by successful refresh or explicit retry.
            }
        }
    }

    /**
     * Refresh driver data from the network.
     * Called on pull-to-refresh or retry.
     */
    fun onRefresh() {
        refreshDrivers(force = true)
    }

    /**
     * Retry loading after an error.
     */
    fun onRetry() {
        applyMutation { copy(error = null) }
        refreshDrivers(force = true)
    }

    /**
     * User selected a driver to view details.
     */
    fun onDriverSelected(driver: Driver) {
        submitEvent(
            DriversEvent.NavigateToDriverDetails(
                driverNumber = driver.driverNumber
            )
        )
    }

    private fun refreshDrivers(force: Boolean) {
        launchWithErrorHandling {
            applyMutation { copy(isRefreshing = force) }
            try {
                driverStore.refreshLatestDrivers(true)
                // Clear error on successful refresh
                applyMutation { copy(error = null) }
            } catch (e: Exception) {
                applyMutation {
                    copy(error = e.message ?: "Failed to load drivers")
                }
                submitEvent(DriversEvent.ShowError(e.message ?: "Failed to load drivers"))
            } finally {
                applyMutation { copy(isRefreshing = false) }
            }
        }
    }
}
