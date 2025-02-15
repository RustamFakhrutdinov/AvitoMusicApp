package com.practicum.avitomusicapp.domain.player

interface PlayerRepository {
    fun isPlaying(): Boolean

    fun preparePlayer(url: String)

    fun getTime(): String

    fun release()

    fun play()

    fun pause()
}