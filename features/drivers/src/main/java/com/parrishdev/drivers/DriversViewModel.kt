package com.parrishdev.drivers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parrishdev.data.F1DriversApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DriversViewModel @Inject constructor(private val f1DriversApi: F1DriversApi) : ViewModel() {
    private val _uiState = MutableStateFlow(DriversViewState(
        events =  {
            when (it) {
                is DriversEvent.DriverSelected -> onDriverSelected(it.driverId)
            }
        }
    ))
    val uiState = _uiState.asStateFlow()

    private val _navigationEvents = MutableSharedFlow<NavigationEvent>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    init {
        fetchDrivers()
    }

    private fun fetchDrivers() {
        viewModelScope.launch {
            val wrapperResult = runCatching {
                f1DriversApi.fetchDrivers()
                    .onSuccess {
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            drivers = it
                        )
                    }
                    .onFailure {
                        _uiState.value = uiState.value.copy(
                            isLoading = false,
                            errorMessage = it.message.orEmpty()
                        )
                    }
            }
            if (wrapperResult.isFailure) {
                _uiState.value = uiState.value.copy(
                    isLoading = false,
                    errorMessage = wrapperResult.exceptionOrNull()?.message.orEmpty()
                )
            }
        }
    }

    fun onDriverSelected(driverId: String) {
        viewModelScope.launch {
            _navigationEvents.emit(NavigationEvent.NavigateToDetails(driverId))
        }
    }
}