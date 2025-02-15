package com.practicum.avitomusicapp.domain.player

import com.practicum.avitomusicapp.domain.models.Track

interface PlayerRepository {
    fun isPlaying(): Boolean

    fun preparePlayer(url: String)

    fun getTime(): String

    fun getCurrentPosition(): Int

    fun release()

    fun play()

    fun pause()

    fun reset()

    fun getDuration(): String

    fun convertFromJsonToList(json: String): ArrayList<Track>

    fun seekTo(progress: Int)
}