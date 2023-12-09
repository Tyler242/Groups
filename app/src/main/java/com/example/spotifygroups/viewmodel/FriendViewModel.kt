package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.FriendRepository
import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.FriendResultModel
import com.example.spotifygroups.uistatemodel.FriendUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FriendViewModel(private val friendRepository: FriendRepository): ViewModel() {
    private val _uiState = MutableStateFlow(FriendUiState())
    val uiState: StateFlow<FriendUiState> = _uiState.asStateFlow()

    init {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val result: FriendResultModel = friendRepository.getFriends()
                _uiState.value = FriendUiState(friends = result.friends)
            }
        }
    }

    fun removeFriend(friend: Friend) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val result = friendRepository.removeFriend(friend.userId)
                _uiState.value = FriendUiState(friends = result.friends)
            }
        }
    }
}