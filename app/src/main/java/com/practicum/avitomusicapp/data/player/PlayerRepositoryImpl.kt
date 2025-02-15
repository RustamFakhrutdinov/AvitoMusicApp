package com.practicum.avitomusicapp.data.player

import android.media.MediaPlayer
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.player.PlayerRepository
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
) : PlayerRepository {
    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun preparePlayer(url: String) {
        if (url != "No previewUrl") {
            try {
                mediaPlayer.setDataSource(url)
                mediaPlayer.prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }

    override fun getTime(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.currentPosition)
    }

    override fun release() {
        if (mediaPlayer != null) {
            mediaPlayer.release()
        }
    }

    override fun reset() {
        mediaPlayer.reset()
    }

    override fun getDuration(): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(mediaPlayer.duration)
    }


    override fun play() {
        mediaPlayer.apply {
            start()
        }
    }

    override fun pause() {
        mediaPlayer.pause()
    }

    override fun convertFromJsonToList(json: String):ArrayList<Track> {

        val itemType = object : TypeToken<List<Track>>() {}.type

        return Gson().fromJson(json, itemType)
    }


}