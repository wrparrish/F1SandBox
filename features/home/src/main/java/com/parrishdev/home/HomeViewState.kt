package com.parrishdev.home

import com.parrishdev.model.RaceWithResultData

data class HomeViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val results: List<RaceWithResultData> = emptyList(),
    val events: (HomeEvent) -> Unit = {}
)

sealed class HomeEvent {
    class MeetingSelected(val meetingId: Int) : HomeEvent()
}

sealed class HomeEffect {
    class GoToMeeting(val meetingId: Int) : HomeEffect()
}


