package com.parrishdev.data

import com.parrishdev.model.Meeting
import com.parrishdev.network.F1Endpoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

interface MeetingsApi {
    suspend fun fetchMeetings(): Result<List<Meeting>>
}

class MeetingsApiImpl @Inject constructor(private val f1Endpoint: F1Endpoint) : MeetingsApi {
    override suspend fun fetchMeetings(): Result<List<Meeting>> {
        return withContext(Dispatchers.IO) {
            runCatching {
                f1Endpoint.getMeetings()
            }
        }
    }

}