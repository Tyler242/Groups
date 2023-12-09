package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.FriendRepository
import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.uistatemodel.FriendSearchUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class FriendSearchViewModel(private val friendRepository: FriendRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendSearchUiState())
    val uiState: StateFlow<FriendSearchUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = FriendSearchUiState(query)
        if (query.length >= 2) {
            getSearchResults()
        }
    }

    private fun getSearchResults() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val data = friendRepository.searchUsers(_uiState.value.query)
                _uiState.value = FriendSearchUiState(_uiState.value.query, data.users)
            }
        }
    }

    fun addFriend(friend: Friend) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                friendRepository.addFriend(friend.userId)
                removeFromSearchResult(friend)
            }
        }
    }

    fun removeFromSearchResult(friend: Friend) {
        val searchResults = _uiState.value.searchResults.filter {
            it != friend
        }
        _uiState.value = FriendSearchUiState(_uiState.value.query, searchResults)
    }
}