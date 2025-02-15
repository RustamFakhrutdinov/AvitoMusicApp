package com.practicum.avitomusicapp.domain.search.impl

import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.search.api.TracksInteractor
import com.practicum.avitomusicapp.domain.search.api.TracksRepository
import com.practicum.avitomusicapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TracksInteractorImpl(private val repository: TracksRepository): TracksInteractor {
    override fun search(expression: String): Flow<Pair<List<Track>?, String?>> {
        return repository.search(expression).map {result ->
            when(result) {
                is Resource.Success -> {
                    Pair(result.data, null)
                }
                is Resource.Error -> {
                    Pair(null, result.message)
                }
            }
        }
    }

    override fun trackListToJson(trackList: List<Track>): String {
        return repository.trackListToJson(trackList)
    }
}