package com.parrishdev.drivers

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parrishdev.data.F1DriversApi
import com.parrishdev.model.DriverRaceResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DriverDetailsViewState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val driverResults: List<DriverRaceResult> = emptyList(),
    val driverId: String? = null // To store and display the driver ID
)

@HiltViewModel
class DriverDetailsViewModel @Inject constructor(
    private val f1DriversApi: F1DriversApi,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DriverDetailsViewState())
    val uiState = _uiState.asStateFlow()

    init {
        savedStateHandle.get<String>(Routes.DriverDetails.ARG_DRIVER_ID)?.let { driverId ->
            _uiState.value = _uiState.value.copy(driverId = driverId)
            fetchDriverHistoricalResults(driverId)
        } ?: run {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = "Driver ID not found."
            )
        }
    }

    private fun fetchDriverHistoricalResults(driverId: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            runCatching {
                f1DriversApi.fetchDriverResults(driverId)
            }.onSuccess { result ->
                result.onSuccess { results ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        driverResults = results
                    )
                }.onFailure { exception ->
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = exception.message ?: "Failed to load driver results."
                    )
                }
            }.onFailure { exception ->
                // This catches issues with the runCatching block itself (e.g., f1DriversApi call failing before Result is returned)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = exception.message ?: "An unexpected error occurred."
                )
            }
        }
    }
}
