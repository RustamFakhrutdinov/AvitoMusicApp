package com.practicum.avitomusicapp.data.search

import android.util.Log
import com.google.gson.Gson
import com.practicum.avitomusicapp.data.NetworkClient
import com.practicum.avitomusicapp.data.dto.ArtistDto
import com.practicum.avitomusicapp.data.dto.TracksResponse
import com.practicum.avitomusicapp.data.dto.TracksSearchRequest
import com.practicum.avitomusicapp.domain.models.Album
import com.practicum.avitomusicapp.domain.models.Artist
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.search.api.TracksRepository
import com.practicum.avitomusicapp.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.util.Locale

class TracksRepositoryImpl(
    private val networkClient: NetworkClient,
    private val json: Gson
) : TracksRepository {
    override fun search(expression: String): Flow<Resource<List<Track>>> = flow {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        when (response.resultCode) {
            -1 -> {
                emit(Resource.Error("Проверьте подключение к интернету"))
            }

            200 -> {
                emit(Resource.Success((response as TracksResponse).data.map {
                    Track(
                        it.id ?: -1,
                        it.title ?: "No track name",
                        SimpleDateFormat("mm:ss", Locale.getDefault()).format(
                            it.duration?.times(
                                1000
                            ) ?: 0
                        ),
                        "https://e-cdns-images.dzcdn.net/images/cover/${it.md5_image}/512x512.jpg",
                        it.preview ?: "No preview",
                        Artist(it.artist?.id ?: -1, it.artist?.name ?: "Автор неизвестен"),
                        Album(
                            it.album?.id ?: -1,
                            it.album?.title,
                            it.album?.cover
                        )
                    )
                }))
            }

            else -> {
                emit(Resource.Error("Ошибка сервера"))
            }
        }
    }

    override fun trackListToJson(trackList: List<Track>): String {
        return json.toJson(trackList)
    }
}