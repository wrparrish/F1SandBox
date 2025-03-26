package com.parrishdev.home

import com.parrishdev.model.Meeting

data class HomeViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val meetings: List<Meeting> = emptyList(),
    val events: (HomeEvent) -> Unit = {}
)

sealed class HomeEvent {
    class MeetingSelected(val meetingId: Int) : HomeEvent()
}

sealed class HomeEffect {
    class GoToMeeting(val meetingId: Int) : HomeEffect()
}


