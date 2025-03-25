package com.parrishdev.home

import com.parrishdev.model.Meeting

data class HomeViewState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val meetings: List<Meeting> = emptyList()
)

sealed class HomeEffect {
    class GoToMeeting(val meetingId: Int) : HomeEffect()
}


