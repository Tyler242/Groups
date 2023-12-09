package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QueueResultModel
import com.example.spotifygroups.uistatemodel.SharedQueueUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Timer
import kotlin.concurrent.schedule

class SharedQueueViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val queueRepository: QueueRepository
) : ViewModel() {
    private val _liveQueue: MutableStateFlow<SharedQueueUiState> = MutableStateFlow(
        SharedQueueUiState()
    )
    val liveQueue: StateFlow<SharedQueueUiState> = _liveQueue.asStateFlow()

    fun playNext(track: Playable, millisecondsLeft: Long, callback: () -> Unit) {
        spotifyRepository.addTrackToQueue(track.uri)
        Timer().schedule(delay = millisecondsLeft) {
            incrementQueue {
                callback()
            }
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
        val countOfTracks = _liveQueue.value.queue.count()
        return countOfTracks > 1
    }

    fun getNextTrack(): Playable {
        return _liveQueue.value.queue[1]
    }

    fun addToLiveQueue(playable: Playable, callback: () -> Unit) {
        val queueResult = queueRepository.addToQueue(playable.id)
        Log.i("SQVM", "queueResult.queue: ${queueResult.queue}")
        Log.i("SQVM", "queueResult.currentTrack: ${queueResult.currentTrack}")
        handleResult(queueResult, callback)
    }

    fun syncLiveQueue(callback: () -> Unit) {
        val queueResult = queueRepository.createQueue()
        handleResult(queueResult, callback)
    }

    fun removeFromLiveQueue(playable: Playable, callback: () -> Unit) {
        val queueResult = queueRepository.removeFromQueue(playable.id)
        handleResult(queueResult, callback)
    }

    fun updateQueue(playable: Playable, index: Int, callback: () -> Unit) {
        val queueResult = queueRepository.updateQueue(playable.id, index)
        handleResult(queueResult, callback)
    }

    fun incrementQueue(callback: () -> Unit) {
        val queueResult = queueRepository.incrementQueue()
        handleResult(queueResult, callback)
    }

    fun playPauseQueue(pause: Boolean, callback: () -> Unit) {
        val queueResult = queueRepository.playPauseQueue(pause)
        handleResult(queueResult, callback)
    }

    private fun handleResult(queueResult: QueueResultModel, callback: () -> Unit) {
        if (queueResult._id === "" && queueResult.creatorId === "") {
            // display error message and use the previous queue
            Log.e("SQVM", "ERROR with queue.")
        } else {
            if (queueResult.queue.isNotEmpty()) {
                val queue = spotifyRepository.getTracks(queueResult.queue)
                _liveQueue.value = SharedQueueUiState(
                    queue,
                    queue[0],
                    queueResult.positionMs,
                    queueResult.isPaused
                )
            } else {
                SharedQueueUiState(positionMs = queueResult.positionMs, isPaused = queueResult.isPaused)
            }
        }
        callback()
    }
}