package com.practicum.avitomusicapp.domain.search.api

import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.util.Resource
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun search(expression: String): Flow<Resource<List<Track>>>
    fun trackListToJson(trackList: List<Track>): String
}