package com.practicum.avitomusicapp.data.network

import com.practicum.avitomusicapp.data.dto.TracksResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface DeezerApi {
    @GET("search")
    suspend fun search(
        @Query("q") query: String
    ): TracksResponse
}