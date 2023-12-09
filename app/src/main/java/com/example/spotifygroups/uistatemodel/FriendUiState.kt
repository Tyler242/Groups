package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Friend

data class FriendUiState(
    val friends: List<Friend> = emptyList()
)