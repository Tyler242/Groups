package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.uistatemodel.SearchTracksUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SearchTracksViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val queueRepository: QueueRepository,
    private val sharedQueueViewModel: SharedQueueViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(SearchTracksUiState())
    val uiState: StateFlow<SearchTracksUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = SearchTracksUiState(query, tracksToAdd = _uiState.value.tracksToAdd)
        if (query.length >= 4) {
            getSearchResults()
        }
    }

    private fun getSearchResults() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val tracks = spotifyRepository.searchTracks(_uiState.value.query)
                _uiState.value =
                    SearchTracksUiState(_uiState.value.query, tracks, _uiState.value.tracksToAdd)
            }
        }
    }

    fun removeFromSearchResult(item: Playable) {
        val tracks = _uiState.value.searchResults.filter {
            it !== item
        }
        _uiState.value =
            SearchTracksUiState(_uiState.value.query, tracks, _uiState.value.tracksToAdd)
    }

    fun addToQueue(playable: Playable) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.addToLiveQueue(playable) {
                    removeFromSearchResult(playable)
                }
            }
        }
    }
}