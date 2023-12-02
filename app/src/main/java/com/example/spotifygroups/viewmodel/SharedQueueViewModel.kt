package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.SpotifyRepository
import com.example.spotifygroups.data.UserRepository
import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QueueResultModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SharedQueueViewModel(
    private val spotifyRepository: SpotifyRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    private val _liveQueue: MutableStateFlow<List<Playable>> = MutableStateFlow(emptyList())
    val liveQueue: StateFlow<List<Playable>> = _liveQueue.asStateFlow()

    fun addToLiveQueue(playable: Playable, callback: () -> Unit) {
        val queueResult = userRepository.addToQueue(playable.id)
        handleResult(queueResult, callback)
    }

    fun syncLiveQueue(callback: () -> Unit) {
        val queueResult = userRepository.createQueue()
        handleResult(queueResult, callback)
    }

    fun removeFromLiveQueue(playable: Playable, callback: () -> Unit) {
        val queueResult = userRepository.removeFromQueue(playable.id)
        handleResult(queueResult, callback)
    }

    fun updateQueue(playable: Playable, index: Int, callback: () -> Unit) {
        val queueResult = userRepository.updateQueue(playable.id, index)
        handleResult(queueResult, callback)
    }

    private fun handleResult(queueResult: QueueResultModel, callback: () -> Unit) {
        if (queueResult._id === "" && queueResult.creatorId === "") {
            // display error message and use the previous queue
            Log.i("SQVM","ERROR with queue.")
        } else {
            val queue = spotifyRepository.getTracks(queueResult.queue)
            _liveQueue.value = queue
        }
        callback()
    }
}