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
    private val sessionViewModel: SessionViewModel,
    private val queueRepository: QueueRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(emptyList<Friend>())
    val uiState: StateFlow<List<Friend>> = _uiState.asStateFlow()

    init {
        getParticipantNames()
    }

    private fun getParticipantNames() {
        val participantIds = sessionViewModel.getParticipants()
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val participants = mutableListOf<Friend>()
                participantIds.forEach {
                    val name = queueRepository.getParticipantNames(it)
                    participants.add(Friend(it, name))
                }
                _uiState.value = participants
            }
        }
    }
}