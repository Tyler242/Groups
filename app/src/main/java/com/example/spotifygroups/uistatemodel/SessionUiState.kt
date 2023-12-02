package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Playable

data class SessionUiState(
    val currentPlaying: Playable? = null,
    val isPaused: Boolean = true,
)