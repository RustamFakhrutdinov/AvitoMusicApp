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
import kotlin.time.Duration

class PlayerViewModel(private val playerInteractor: PlayerInteractor
) : ViewModel() {
    private lateinit var track: Track
    private val tracksList = arrayListOf<Track>()

    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private val playScreenLiveData = MutableLiveData<Track>()
    fun getPlayScreenLiveData(): LiveData<Track> = playScreenLiveData

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
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = playerInteractor.getTime()))
            }
            if(playerInteractor.getTime() == playerInteractor.getDuration()) {
                playStatusLiveData.postValue(
                    getCurrentPlayStatus().copy(
                        progress = duration,
                        isPlaying = false
                    )
                )
            }
        }
    }


    fun pause() {
        playerInteractor.pause()
        timerJob?.cancel()
       // timerJob?.cancel()
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

}