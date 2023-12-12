package com.example.spotifygroups.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.QueueRepository
import com.example.spotifygroups.datamodel.FriendQueue
import com.example.spotifygroups.uistatemodel.HomeUiState
import com.example.spotifygroups.uistatemodel.QueueToJoin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HomeViewModel(private val queueRepository: QueueRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        getAvailableQueues()
    }

    private fun getAvailableQueues() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val friendQueues = queueRepository.getFriendQueues()
                val queues =
                    friendQueues.queues.map { QueueToJoin(it.id, it.creatorId, it.creatorName) }
                _uiState.value = HomeUiState(queues)
                Log.i("HVM", queues.toString())
            }
        }
    }
}