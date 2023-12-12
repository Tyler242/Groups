package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.datamodel.QueueItem

data class SharedQueueUiState(
    val queue: List<QueueItem> = emptyList(),
    val currentTrack: QueueItem? = null,
    val positionMs: Long? = null,
    val isPaused: Boolean = true,
    val participants: List<String> = emptyList(),
    val creatorId: String = ""
)