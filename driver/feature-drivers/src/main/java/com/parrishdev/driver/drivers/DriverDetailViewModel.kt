package com.parrishdev.driver.drivers

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.toRoute
import com.parrishdev.common.udf.BaseStateViewModel
import com.parrishdev.common.udf.ViewModelBundle
import com.parrishdev.driver.contracts.DriverDetailsRoute
import com.parrishdev.driver.store.DriverStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * ViewModel for the Driver Detail screen.
 *
 * Follows UDF pattern:
 * - [DriverDetailDataState]: Internal state with raw data
 * - [DriverDetailViewState]: UI-ready state (via [DriverDetailStateProvider])
 */
@HiltViewModel
class DriverDetailViewModel @Inject constructor(
    private val driverStore: DriverStore,
    stateProvider: DriverDetailStateProvider,
    viewModelBundle: ViewModelBundle,
    savedStateHandle: SavedStateHandle
) : BaseStateViewModel<DriverDetailDataState, DriverDetailViewState>(
    initialDataState = run {
        val route = savedStateHandle.toRoute<DriverDetailsRoute>()
        DriverDetailDataState(driverNumber = route.driverNumber, season = route.season)
    },
    stateProvider = stateProvider,
    viewModelBundle = viewModelBundle
) {
    private val route = savedStateHandle.toRoute<DriverDetailsRoute>()
    val driverNumber: Int = route.driverNumber
    val season: Int = route.season

    init {
        refreshDriver(force = false)
    }

    override fun onStartObserving(lifecycleOwner: LifecycleOwner) {
        observeWithLifecycle(
            lifecycleOwner = lifecycleOwner,
            flow = driverStore.streamDriver(driverNumber, season)
        ) { driver ->
            applyMutation {
                copy(driver = driver)
            }
        }
    }

    fun onRefresh() {
        refreshDriver(force = true)
    }

    fun onRetry() {
        applyMutation { copy(error = null) }
        refreshDriver(force = true)
    }

    private fun refreshDriver(force: Boolean) {
        launchWithErrorHandling {
            applyMutation { copy(isRefreshing = force) }
            try {
                // Refresh latest drivers standings to update DB cache.
                driverStore.refreshLatestDrivers(season = season, force = force)
                applyMutation { copy(error = null) }
            } catch (e: Exception) {
                applyMutation {
                    copy(error = e.message ?: "Failed to load driver details")
                }
            } finally {
                applyMutation { copy(isRefreshing = false) }
            }
        }
    }
}
