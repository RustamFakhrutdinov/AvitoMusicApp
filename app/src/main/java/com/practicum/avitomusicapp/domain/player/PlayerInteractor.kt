package com.practicum.avitomusicapp.domain.player

import com.practicum.avitomusicapp.domain.models.Track

interface PlayerInteractor {
    fun isPlaying(): Boolean

    fun preparePlayer(url: String)

    fun getTime(): String

    fun getCurrentPosition(): Int

    fun play()

    fun pause()

    fun release()

    fun reset()

    fun getDuration(): String

    fun convertFromJsonToList(json: String): ArrayList<Track>

    fun seekTo(progress: Int)
}