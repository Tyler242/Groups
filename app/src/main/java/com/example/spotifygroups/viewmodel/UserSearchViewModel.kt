package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.data.FriendRepository
import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.UserModel
import com.example.spotifygroups.uistatemodel.UserSearchUiState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UserSearchViewModel(private val friendRepository: FriendRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(UserSearchUiState())
    val uiState: StateFlow<UserSearchUiState> = _uiState.asStateFlow()

    fun updateQuery(query: String) {
        _uiState.value = UserSearchUiState(query)
        if (query.length >= 2) {
            getSearchResults()
        }
    }

    private fun getSearchResults() {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                val data = friendRepository.searchUsers(_uiState.value.query)
                _uiState.value = UserSearchUiState(_uiState.value.query, data.users)
            }
        }
    }

    fun addFriend(user: UserModel) {
        runBlocking {
            CoroutineScope(Dispatchers.IO).launch {
                friendRepository.addFriend(user.id)
                removeFromSearchResult(user)
            }
        }
    }

    private fun removeFromSearchResult(user: UserModel) {
        val searchResults = _uiState.value.searchResults.filter {
            it != user
        }
        _uiState.value = UserSearchUiState(_uiState.value.query, searchResults)
    }
}