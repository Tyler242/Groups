package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.datamodel.QueueItem
import com.example.spotifygroups.datamodel.QueueResultModel
import com.example.spotifygroups.uistatemodel.SharedQueueUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.lang.IndexOutOfBoundsException
import java.util.Timer
import kotlin.concurrent.schedule

class SharedQueueViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val queueRepository: QueueRepository,
    private val resetCallback: () -> Unit
) : ViewModel() {
    private val _liveQueue: MutableStateFlow<SharedQueueUiState> = MutableStateFlow(
        SharedQueueUiState()
    )
    val liveQueue: StateFlow<SharedQueueUiState> = _liveQueue.asStateFlow()

    fun playNext(millisecondsLeft: Long, callback: () -> Unit) {
        spotifyRepository.addTrackToQueue(_liveQueue.value.currentTrack.next!!.uri)
        Timer().schedule(delay = millisecondsLeft) {
            incrementQueue {
                callback()
            }
        }
    }

    fun skip(callback: () -> Unit) {
        incrementQueue {
            spotifyRepository.playTrack(_liveQueue.value.currentTrack.next!!, false)
            callback()
        }
    }

    fun pauseAfterDelay(millisecondsLeft: Long, callback: () -> Unit) {
        Timer().schedule(delay = millisecondsLeft) {
            spotifyRepository.pause(false)
            incrementQueue {
                callback()
            }
        }
    }

    fun hasNextTrack(): Boolean {
        return _liveQueue.value.currentTrack.next !== null
    }

    fun getNextTrack(): QPlayable {
        return _liveQueue.value.currentTrack.next!!
    }

    fun addToLiveQueue(playable: QPlayable, callback: () -> Unit) {
        val queueResult = queueRepository.addToQueue(playable)
        handleResult(queueResult, callback)
    }

    fun syncLiveQueue(callback: () -> Unit) {
        if (queueRepository.isQueueIdSet()) {
            val queueResult = queueRepository.getQueue()
            if (queueResult.second.participant) {
                handleResult(queueResult.first, callback)
            } else {
                resetCallback()
                reset()
            }
        } else {
            val queueResult = queueRepository.createQueue()
            handleResult(queueResult, callback)
        }
    }

    fun removeFromLiveQueue(playable: QPlayable, callback: () -> Unit) {
        val queueResult = queueRepository.removeFromQueue(playable.spotifyId)
        handleResult(queueResult, callback)
    }

    fun updateQueue(playable: QPlayable, index: Int, callback: () -> Unit) {
        val queueResult = queueRepository.updateQueue(playable.spotifyId, index)
        handleResult(queueResult, callback)
    }

    private fun incrementQueue(callback: () -> Unit) {
        val queueResult = queueRepository.incrementQueue()
        handleResult(queueResult, callback)
    }

    fun playPauseQueue(pause: Boolean, callback: () -> Unit) {
        val queueResult = queueRepository.playPauseQueue(pause)
        handleResult(queueResult, callback)
    }

    private fun handleResult(queueResult: QueueResultModel, callback: () -> Unit) {
        if (queueResult.id === "" && queueResult.creatorId === "") {
            // display error message and use the previous queue
            Log.e("SQVM", "ERROR with queue.")
        } else {
            try {
                Log.i("SQVM", "${queueResult.currentTrack}")
                if (queueResult.queue.isNotEmpty()) {
                    _liveQueue.value = SharedQueueUiState(
                        queueResult.queue,
                        queueResult.currentTrack,
                        queueResult.positionMs,
                        queueResult.isPaused,
                        queueResult.participants,
                        queueResult.creatorId
                    )
                } else {
                    _liveQueue.value = SharedQueueUiState(
                        positionMs = queueResult.positionMs,
                        isPaused = queueResult.isPaused,
                        participants = queueResult.participants,
                        creatorId = queueResult.creatorId
                    )
                }
            } catch (ex: IndexOutOfBoundsException) {
                ex.printStackTrace()
                return
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }
        callback()
    }

    fun reset() {
        _liveQueue.value = SharedQueueUiState()
        queueRepository.reset()
    }
}