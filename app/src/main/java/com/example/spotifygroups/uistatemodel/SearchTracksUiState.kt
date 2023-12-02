package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Playable

data class SearchTracksUiState(
    val query: String = "",
    val searchResults: List<Playable> = emptyList(),
    val tracksToAdd: List<Playable> = emptyList()
)