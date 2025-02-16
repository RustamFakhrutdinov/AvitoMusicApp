package com.practicum.avitomusicapp.ui.downloads

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.avitomusicapp.domain.downloads.DownloadsInteractor
import com.practicum.avitomusicapp.domain.models.Album
import com.practicum.avitomusicapp.domain.models.Artist
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.ui.state.SearchState
import com.practicum.avitomusicapp.util.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadsViewModel(private val downloadsInteractor: DownloadsInteractor,
    application: Application) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData
    private var latestSearchText: String? = null

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchLocalTracks(changedText)
        }

    fun searchDebounce(changedText: String, isButtonPressed: Boolean) {
        if(changedText == "") {
            initAfterPermission()
        } else{
            if (latestSearchText != changedText) {
                latestSearchText = changedText
                if (!isButtonPressed) {
                    trackSearchDebounce(changedText)
                } else {
                    searchLocalTracks(changedText)
                }
            }
        }
    }

    private fun searchLocalTracks(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tracks = downloadsInteractor.findTracksFromLocalStorage(query)
            if (tracks.isEmpty()) {
                stateLiveData.postValue(
                    SearchState.Empty("Треки не найдены")
                )
            } else {
                stateLiveData.postValue(SearchState.Content(tracks))
            }
        }
    }

    fun initAfterPermission() {
        val allTracks = downloadsInteractor.getAllTracks()
        stateLiveData.postValue(SearchState.Content(allTracks))
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
    }
}