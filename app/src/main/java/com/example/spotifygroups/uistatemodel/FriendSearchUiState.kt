package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Friend

data class FriendSearchUiState(
    val query: String = "",
    val searchResults: List<Friend> = emptyList()
)