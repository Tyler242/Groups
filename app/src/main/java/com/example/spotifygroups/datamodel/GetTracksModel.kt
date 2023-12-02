package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class GetTracksModel(
    @SerializedName("tracks") val tracks: List<Playable> = emptyList()
)