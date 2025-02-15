package com.practicum.avitomusicapp.domain.player.impl

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

    override fun play() {
        repository.play()
    }

    override fun pause() {
        repository.pause()
    }

    override fun release() {
        repository.release()
    }
}