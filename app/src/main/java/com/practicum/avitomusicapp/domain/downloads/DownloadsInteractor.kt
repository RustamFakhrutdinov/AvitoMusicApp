package com.practicum.avitomusicapp.domain.downloads

import com.practicum.avitomusicapp.domain.models.Track

interface DownloadsInteractor {
    fun getAllTracks(): List<Track>
    fun findTracksFromLocalStorage(query: String): List<Track>
}