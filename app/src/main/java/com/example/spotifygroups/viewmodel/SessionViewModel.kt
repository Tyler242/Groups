package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.QPlayable
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
                    _uiState.value = setUiState()
                    setNextItemCallback()
                }
            }
        }
    }

    private fun setNextItemCallback() {
        Timer().scheduleAtFixedRate(0, 1000) {
            if (!isAddingToQueue) {
                addNextToSpotifyQueue()
                sharedQueueViewModel.syncLiveQueue {
                    if (sharedQueueViewModel.liveQueue.value.isPaused) {
                        _uiState.value = setUiState()
                        spotifyRepository.pause(false)
                    } else {
                        _uiState.value = setUiState()
                        spotifyRepository.resume(false)
                    }
                }
            }
        }
    }

    private fun addNextToSpotifyQueue() {
        try {
            if (!spotifyRepository.isSpotifyTrackLoaded()) {
                return
            }
            val currentPlayingData = spotifyRepository.getCurrentPlayingTrack() ?: return
            val spotifyTrack = currentPlayingData.first
            val positionMs = currentPlayingData.second

            // check if current track is in the queue
            val trackInQueue = sharedQueueViewModel.liveQueue.value.queue.any {
                it.playable!!.name == spotifyTrack.name && it.playable.duration == spotifyTrack.duration
            }
            if (trackInQueue) {
                // the current playing track is in the queue
                val millisecondsLeft = spotifyTrack.duration - positionMs
                if (millisecondsLeft <= 5000) {
                    isAddingToQueue = true
                    if (sharedQueueViewModel.hasNextTrack()) {
                        sharedQueueViewModel.playNext(millisecondsLeft) {
                            isAddingToQueue = false
                        }
                    } else {
                        sharedQueueViewModel.pauseAfterDelay(millisecondsLeft) {
                            isAddingToQueue = false
                        }
                    }
                }
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
            return
        }
    }

    fun skip() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                isAddingToQueue = true
                Log.i("SVM - Skip", sharedQueueViewModel.hasNextTrack().toString())
                if (sharedQueueViewModel.hasNextTrack()) {
                    sharedQueueViewModel.skip {
                        isAddingToQueue = false
                    }
                } else {
                    isAddingToQueue = false
                }
            }
        }
    }


    fun removeFromQueue(playable: QPlayable, index: Int) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.removeFromLiveQueue(playable) {
                    _uiState.value = setUiState()
                }
            }
        }
    }

    fun updateQueue(playable: QPlayable, index: Int) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.updateQueue(playable, index) {
                    _uiState.value = setUiState()
                }
            }
        }
    }

    fun play() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.syncLiveQueue {
                    // check what is on the current spotify value
                    val sTrack = spotifyRepository.getCurrentPlayingTrack()
                    val qTrack = sharedQueueViewModel.liveQueue.value.currentTrack.playable
                    if (qTrack !== null && sTrack !== null) {
                        if (sTrack.first.name == qTrack.name
                            && sTrack.first.duration == qTrack.duration) {
                            sharedQueueViewModel.playPauseQueue(false) {
                                _uiState.value = setUiState(false)
                                spotifyRepository.resume(false)
                            }
                        } else {
                            sharedQueueViewModel.playPauseQueue(false) {
                                _uiState.value = setUiState(false)
                                spotifyRepository.playTrack(qTrack, false)
                            }
                        }
                    } else if (sTrack == null && qTrack !== null) {
                        sharedQueueViewModel.playPauseQueue(false) {
                            _uiState.value = setUiState(false)
                            spotifyRepository.playTrack(qTrack, false)
                        }
                    } else {
                        Log.i("SVM", "Please add tracks")
                    }
                }
            }
        }
    }

    fun pause() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                sharedQueueViewModel.playPauseQueue(true) {
                    _uiState.value = setUiState(true)
                    spotifyRepository.pause(false)
                }
            }
        }
    }

    fun getParticipants(): List<Friend> {
        return sharedQueueViewModel.liveQueue.value.participants
    }

    fun leaveSession(callback: (Boolean) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val successResponse = queueRepository.leaveQueue()
                callback(successResponse)
            }
        }
    }

    fun deleteSession(callback: (Boolean) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val successResponse = queueRepository.deleteQueue()
                callback(successResponse)
            }
        }
    }

    private fun setUiState(isPaused: Boolean? = null): SessionUiState {
        return SessionUiState(
            if (isPaused !== null) isPaused else sharedQueueViewModel.liveQueue.value.isPaused,
            sharedQueueViewModel.liveQueue.value.participants,
            sharedQueueViewModel.liveQueue.value.creatorId
        )
    }

    fun reset() {
        _uiState.value = SessionUiState()
        sharedQueueViewModel.reset()
    }
}