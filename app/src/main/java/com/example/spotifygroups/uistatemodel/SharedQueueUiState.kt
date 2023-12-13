package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Friend
import com.example.spotifygroups.datamodel.QPlayable
import com.example.spotifygroups.datamodel.QueueItem

data class SharedQueueUiState(
    val queue: List<QueueItem> = emptyList(),
    val currentTrack: QueueItem = QueueItem(),
    val positionMs: Long? = null,
    val isPaused: Boolean = true,
    val participants: List<Friend> = emptyList(),
    val creatorId: String = ""
)