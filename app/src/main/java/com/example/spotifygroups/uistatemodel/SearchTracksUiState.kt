package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.Playable
import com.example.spotifygroups.datamodel.QPlayable

data class SearchTracksUiState(
    val query: String = "",
    val searchResults: List<QPlayable> = emptyList()
)