package com.practicum.avitomusicapp.ui.player

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.player.PlayerInteractor
import com.practicum.avitomusicapp.ui.player.state.PlayStatus
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration

class PlayerViewModel(private val playerInteractor: PlayerInteractor
) : ViewModel() {
    private lateinit var track: Track
    private val tracksList = arrayListOf<Track>()

    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private val playScreenLiveData = MutableLiveData<Track>()
    fun getPlayScreenLiveData(): LiveData<Track> = playScreenLiveData

    private val seekBarProgressLiveData = MutableLiveData<Int>()
    fun getSeekBarProgressLiveData(): LiveData<Int> = seekBarProgressLiveData

    private var timerJob: Job? = null

    override fun onCleared() {
        super.onCleared()
        release()
    }


    fun play() {
        playerInteractor.play()
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
        startTimer(track.duration)
    }

    private fun startTimer(duration: String) {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                val currentPosition = playerInteractor.getCurrentPosition()
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = playerInteractor.getTime()))
                seekBarProgressLiveData.postValue(currentPosition)
            }
            if(playerInteractor.getTime() == playerInteractor.getDuration()) {
                seekBarProgressLiveData.postValue(timeToMilliseconds(track.duration))
                playStatusLiveData.postValue(
                    getCurrentPlayStatus().copy(
                        isPlaying = false
                    )
                )
            }
        }
    }


    fun pause() {
        playerInteractor.pause()
        timerJob?.cancel()
        playStatusLiveData.postValue(getCurrentPlayStatus().copy(isPlaying = false))
    }

    private fun getCurrentPlayStatus(): PlayStatus {
        return playStatusLiveData.value ?: PlayStatus(
            progress = "00:00",
            isPlaying = false,
        )
    }

    private fun release() {
        playerInteractor.release()
    }

    private fun reset() {
        playerInteractor.reset()
    }

    private fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url)
    }

    fun jsonToList(json: String) {
        tracksList.addAll(playerInteractor.convertFromJsonToList(json))
    }

    fun fastForwardPressed() {
        reset()
        val trackIndex = tracksList.indexOf(track)
        if (trackIndex >= tracksList.lastIndex) {
            track = tracksList[0]
            playScreenLiveData.postValue(track)
        } else {
            track = tracksList[trackIndex+1]
            playScreenLiveData.postValue(track)
        }
        preparePlayer(track.preview)
        play()
    }

    fun rewindPressed() {
        reset()
        val trackIndex = tracksList.indexOf(track)
        if (trackIndex <= 0) {
            track = tracksList[tracksList.lastIndex]
            playScreenLiveData.postValue(track)
        } else {
            track = tracksList[trackIndex-1]
            playScreenLiveData.postValue(track)
        }
        preparePlayer(track.preview)
        play()
    }

    fun preparePlayerScreen(trackFromSearch: Track) {
        track = trackFromSearch
        preparePlayer(track.preview)
        playScreenLiveData.postValue(track)
        play()
    }

    fun seekTo(progress: Int) {
        playerInteractor.seekTo(progress)
        seekBarProgressLiveData.postValue(progress)
    }

    fun timeToMilliseconds(time: String): Int {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        val date: Date = dateFormat.parse(time) ?: return 0 // Если парсинг не удался, вернем 0
        return date.time.toInt()
    }

}