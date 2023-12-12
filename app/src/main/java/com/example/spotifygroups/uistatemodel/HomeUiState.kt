package com.example.spotifygroups.uistatemodel

data class HomeUiState(
    val queuesToJoin: List<QueueToJoin> = emptyList(),
)

data class QueueToJoin(
    val queueId: String,
    val creatorId: String,
    val creatorName: String? = null
)
