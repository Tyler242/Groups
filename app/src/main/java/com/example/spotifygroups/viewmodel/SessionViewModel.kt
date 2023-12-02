package com.example.spotifygroups.viewmodel

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.UserRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.uistatemodel.SessionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SessionViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val userRepository: UserRepository,
    private val sharedQueueViewModel: SharedQueueViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()

    init {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.syncLiveQueue {
                    spotifyRepository.getPlayState {
                        _uiState.value = SessionUiState(
                            sharedQueueViewModel.liveQueue.value[0],
                            isPaused = it,
                        )
                    }
                }
            }
        }
    }

    fun removeFromQueue(playable: Playable, index: Int) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.removeFromLiveQueue(playable) {
                    _uiState.value = SessionUiState(
                        sharedQueueViewModel.liveQueue.value[0],
                        _uiState.value.isPaused
                    )
                }
            }
        }
    }

    fun updateQueue(playable: Playable, index: Int) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.updateQueue(playable, index) {
                    _uiState.value = SessionUiState(
                        _uiState.value.currentPlaying,
                        _uiState.value.isPaused
                    )
                }
            }
        }
    }

    fun updatePlayState() {
        spotifyRepository.updatePlayState {
            _uiState.value = SessionUiState(
                _uiState.value.currentPlaying,
                it
            )
        }
    }


}