package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Playable

data class SharedQueueUiState(
    val queue: List<Playable> = emptyList(),
    val currentTrack: Playable? = null,
    val positionMs: Long? = null,
    val isPaused: Boolean = true,
)