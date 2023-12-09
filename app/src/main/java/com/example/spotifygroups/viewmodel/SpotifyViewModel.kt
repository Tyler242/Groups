package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.uistatemodel.SpotifyUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SpotifyViewModel(private val spotifyRepository: SpotifyRepository, private val queueRepository: QueueRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(SpotifyUiState())
    val uiState : StateFlow<SpotifyUiState> = _uiState.asStateFlow()

    init {
        disconnect()
        connectApp()
    }

    fun saveToken(token: String) {
        spotifyRepository.saveToken(token)
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                spotifyRepository.getSpotifyUserProfile()
                queueRepository.authenticate(spotifyRepository.loggedInUser)
                _uiState.value = SpotifyUiState(spotifyRepository.getToken(), spotifyRepository.checkToken())
            }
        }
    }

    private fun connectApp() {
        spotifyRepository.connectApp()
    }

    fun disconnect() {
        spotifyRepository.disconnect()
    }
}