package com.practicum.avitomusicapp.domain.player.impl

import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.player.PlayerInteractor
import com.practicum.avitomusicapp.domain.player.PlayerRepository

class PlayerInteractorImpl(private val repository: PlayerRepository) : PlayerInteractor {
    override fun isPlaying(): Boolean {
        return repository.isPlaying()
    }

    override fun preparePlayer(url: String) {
        repository.preparePlayer(url)
    }

    override fun getTime(): String {
        return repository.getTime()
    }

    override fun getCurrentPosition(): Int {
        return repository.getCurrentPosition()
    }

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }

    override fun reset() {
        repository.reset()
    }

    override fun getDuration(): String {
       return repository.getDuration()
    }

    override fun convertFromJsonToList(json: String): ArrayList<Track> {
        return repository.convertFromJsonToList(json)
    }

    override fun seekTo(progress: Int) {
        repository.seekTo(progress)
    }
}