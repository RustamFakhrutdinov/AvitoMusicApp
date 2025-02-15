package com.practicum.avitomusicapp.domain.player

interface PlayerInteractor {
    fun isPlaying(): Boolean

    fun preparePlayer(url: String)

    fun getTime(): String

    fun play()

    fun pause()

    fun release()
}