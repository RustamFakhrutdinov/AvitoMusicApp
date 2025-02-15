package com.practicum.avitomusicapp.domain.search.api

import com.practicum.avitomusicapp.domain.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun search(expression: String): Flow<Pair<List<Track>?, String?>>
    fun trackListToJson(trackList: List<Track>): String
}