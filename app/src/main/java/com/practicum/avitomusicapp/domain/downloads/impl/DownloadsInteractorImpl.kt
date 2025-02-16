package com.practicum.avitomusicapp.domain.downloads.impl

import com.practicum.avitomusicapp.domain.downloads.DownloadsInteractor
import com.practicum.avitomusicapp.domain.downloads.DownloadsRepository
import com.practicum.avitomusicapp.domain.models.Track

class DownloadsInteractorImpl(private val repository: DownloadsRepository): DownloadsInteractor {
    override fun getAllTracks(): List<Track> {
        return repository.getAllTracks()
    }

    override fun findTracksFromLocalStorage(query: String): List<Track> {
        return repository.findTracksFromLocalStorage(query)
    }
}