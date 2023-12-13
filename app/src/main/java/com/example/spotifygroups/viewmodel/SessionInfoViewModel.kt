package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.datamodel.Friend
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class SessionInfoViewModel(
    private val queueRepository: QueueRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(emptyList<Friend>())
    val uiState: StateFlow<List<Friend>> = _uiState.asStateFlow()

    init {
        getParticipantNames()
    }

    fun getParticipantNames() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val queueResult = queueRepository.getQueue()
                if (queueResult.second.participant) {
                    _uiState.value = queueResult.first.participants
                }
            }
        }
    }

    fun removeParticipant(participant: Friend, callback: (Boolean) -> Unit) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                callback(queueRepository.removeParticipant(participant.userId))
            }
        }
    }
}