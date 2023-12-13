package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.Playable

data class SessionUiState(
    val isPaused: Boolean = true,
    val participants: List<Friend> = emptyList(),
    val creatorId: String = ""
)