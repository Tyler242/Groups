package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.uistatemodel.SessionUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Timer
import kotlin.concurrent.scheduleAtFixedRate

class SessionViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val queueRepository: QueueRepository,
    private val sharedQueueViewModel: SharedQueueViewModel
) : ViewModel() {
    private val _uiState = MutableStateFlow(SessionUiState())
    val uiState: StateFlow<SessionUiState> = _uiState.asStateFlow()
    private var isAddingToQueue: Boolean = false

    init {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.syncLiveQueue {
                    _uiState.value = SessionUiState(
                        isPaused = true,
                    )
                    setNextItemCallback()
                }
            }
        }
    }

    private fun setNextItemCallback() {
        Timer().scheduleAtFixedRate(0, 500) {
            if (!isAddingToQueue) {
                addNextToSpotifyQueue()
                sharedQueueViewModel.syncLiveQueue {
                    _uiState.value = SessionUiState(sharedQueueViewModel.liveQueue.value.isPaused)
                }
            }
        }
    }

    private fun addNextToSpotifyQueue() {
        val currentPlayingData = spotifyRepository.getCurrentPlayingTrack()
        val spotifyTrack = currentPlayingData.first
        val positionMs = currentPlayingData.second

        // check if current track is in the queue
        val trackInQueue = sharedQueueViewModel.liveQueue.value.queue.any {
            it.name == spotifyTrack.name && it.duration == spotifyTrack.duration
        }
        if (trackInQueue) {
            // the current playing track is in the queue
            val millisecondsLeft = spotifyTrack.duration - positionMs
            if (millisecondsLeft <= 5000) {
                isAddingToQueue = true
                if (sharedQueueViewModel.hasNextTrack()) {
                    val nextTrack = sharedQueueViewModel.getNextTrack()
                    sharedQueueViewModel.playNext(nextTrack, millisecondsLeft) {
                        isAddingToQueue = false
                    }
                } else {
                    sharedQueueViewModel.pauseAfterDelay(millisecondsLeft) {
                        isAddingToQueue = false
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
                        _uiState.value.isPaused
                    )
                }
            }
        }
    }

    fun updatePlayState() {
        // could be called for paused or playing
        // sync the queue
        // if we are playing then check to make sure there is something in the queue.
        // if we are pausing, go ahead and pause.
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.syncLiveQueue {
                    val isPaused = sharedQueueViewModel.liveQueue.value.isPaused
                    if (isPaused) {
                        // user is trying to begin or continue playback
                        // check if there are tracks in the queue.
                        val queueCurrentTrack = sharedQueueViewModel.liveQueue.value.currentTrack
                        val isQueueEmpty = sharedQueueViewModel.liveQueue.value.queue.isEmpty()
                        if (!isQueueEmpty && queueCurrentTrack !== null) {
                            // there are tracks in the queue
                            // check the current spotify track
                            val currentSpotifyTrack =
                                spotifyRepository.getCurrentPlayingTrack().first
                            if (currentSpotifyTrack.name !== queueCurrentTrack.name &&
                                currentSpotifyTrack.duration != queueCurrentTrack.duration
                            ) {
                                // spotify track is not the current queue track.
                                sharedQueueViewModel.playPauseQueue(false) {
                                    _uiState.value = SessionUiState(false)
                                    spotifyRepository.playTrack(queueCurrentTrack, false)
                                }
                            } else {
                                // spotify track is the current queue track.
                                sharedQueueViewModel.playPauseQueue(false) {
                                    _uiState.value = SessionUiState(false)
                                    spotifyRepository.resume(false)
                                }
                            }
                        } else {
                            // no tracks in the queue
                            Log.i("SVM", "Tracks need to be added to the queue before playing")
                        }
                    } else {
                        // user is trying to pause playback
                        sharedQueueViewModel.playPauseQueue(true) {
                            _uiState.value = SessionUiState(true)
                            spotifyRepository.pause(false)
                        }
                    }
                }
            }
        }
    }
}