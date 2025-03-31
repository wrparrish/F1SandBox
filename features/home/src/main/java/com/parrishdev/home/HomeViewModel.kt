package com.parrishdev.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.parrishdev.data.MeetingsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val meetingsApi: MeetingsApi) : ViewModel() {
    private val _viewState: MutableStateFlow<HomeViewState> = MutableStateFlow(HomeViewState(
        events = {
            when (it) {
                is HomeEvent.MeetingSelected -> onMeetingSelected(it.meetingId)
            }
        }
    ))
    val viewState: StateFlow<HomeViewState> = _viewState.asStateFlow()

    private val _viewEffects: MutableSharedFlow<HomeEffect> = MutableSharedFlow()
    val viewEffects: SharedFlow<HomeEffect> = _viewEffects

    init {
        fetchRaceResults()
    }

    private fun fetchRaceResults() {
        viewModelScope.launch {
            val wrappedResult = runCatching {
                meetingsApi.fetchRaceResults()
            }.onSuccess {
                _viewState.value = viewState.value.copy(
                    isLoading = false,
                    results = it.getOrThrow()
                )
            }.onFailure {
                _viewState.value = viewState.value.copy(
                    isLoading = false,
                    errorMessage = it.message.orEmpty()
                )
            }

            if (wrappedResult.isFailure) {
                _viewState.value = viewState.value.copy(
                    isLoading = false,
                    errorMessage = wrappedResult.exceptionOrNull()?.message.orEmpty()
                )
            }
        }
    }
    

    private fun onMeetingSelected(meetingKey: Int) {
        viewModelScope.launch {
            _viewEffects.emit(HomeEffect.GoToMeeting(meetingKey))
        }
    }
}
