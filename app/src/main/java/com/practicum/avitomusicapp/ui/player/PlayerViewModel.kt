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

class PlayerViewModel(private val playerInteractor: PlayerInteractor,
                      private val context: Context
) : ViewModel() {
    private val playStatusLiveData = MutableLiveData<PlayStatus>()
    fun getPlayStatusLiveData(): LiveData<PlayStatus> = playStatusLiveData

    private var timerJob: Job? = null

    private lateinit var clickDebounce: (Track) -> Unit

    override fun onCleared() {
        super.onCleared()
        release()
    }

    fun play(duration: String) {
        playerInteractor.play()
        playStatusLiveData.value = getCurrentPlayStatus().copy(isPlaying = true)
        startTimer(duration)
    }

    private fun startTimer(duration: String) {
        timerJob = viewModelScope.launch {
            while (playerInteractor.isPlaying()) {
                delay(300L)
                playStatusLiveData.postValue(getCurrentPlayStatus().copy(progress = playerInteractor.getTime()))
            }
            playStatusLiveData.postValue(
                getCurrentPlayStatus().copy(
                    progress = duration,
                    isPlaying = false
                )
            )
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
            isFavourite = false
        )
    }

    private fun release() {
        playerInteractor.release()
    }

    fun preparePlayer(url: String) {
        playerInteractor.preparePlayer(url)
    }

}