package com.practicum.avitomusicapp.ui.search

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.avitomusicapp.R
import com.practicum.avitomusicapp.domain.models.Track
import com.practicum.avitomusicapp.domain.search.api.TracksInteractor
import com.practicum.avitomusicapp.ui.state.SearchState
import com.practicum.avitomusicapp.util.SingleLiveEvent
import com.practicum.avitomusicapp.util.debounce
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SearchViewModel(
    private val tracksInteractor: TracksInteractor,
    application: Application
) : AndroidViewModel(application) {

    private val stateLiveData = MutableLiveData<SearchState>()
    fun observeState(): LiveData<SearchState> = stateLiveData


    private val showToast = SingleLiveEvent<String>()
    fun observeShowToast(): LiveData<String> = showToast

    private var latestSearchText: String? = null

    override fun onCleared() {
    }

    private val trackSearchDebounce =
        debounce<String>(SEARCH_DEBOUNCE_DELAY, viewModelScope, true) { changedText ->
            searchRequest(changedText)
        }

    fun searchDebounce(changedText: String, isButtonPressed: Boolean) {
        if (latestSearchText != changedText) {
            latestSearchText = changedText
            if (!isButtonPressed) {
                trackSearchDebounce(changedText)
            } else {
                searchRequest(changedText)
            }
        }
    }

    private fun searchRequest(inputEditText: String) {
        if (inputEditText.isNotEmpty()) {
            renderState(SearchState.Loading)

            viewModelScope.launch {
                tracksInteractor
                    .search(inputEditText)
                    .collect { pair ->
                        val tracks = mutableListOf<Track>()
                        val foundTracks = pair.first
                        val errorMessage = pair.second
                        if (foundTracks != null) {
                            tracks.addAll(foundTracks)
                        }

                        when {
                            errorMessage != null -> {
                                renderState(
                                    SearchState.Error(
                                        errorMessage = getApplication<Application>().getString(R.string.something_went_wrong),
                                    )
                                )
                                showToast.postValue(errorMessage)
                            }

                            tracks.isEmpty() -> {
                                renderState(
                                    SearchState.Empty(
                                        message = getApplication<Application>().getString(R.string.nothing_found),
                                    )
                                )
                            }

                            else -> {
                                renderState(
                                    SearchState.Content(
                                        tracks = tracks,
                                    )
                                )
                            }
                        }
                    }
            }
        }
    }

    private fun renderState(state: SearchState) {
        stateLiveData.postValue(state)
    }

    fun trackListToJson(trackList: List<Track>): String {
        return tracksInteractor.trackListToJson(trackList)
    }

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 2000L
        private const val SEARCH_DEBOUNCE_DELAY_FOR_AUTO_SEARCH = 3000L
    }


}