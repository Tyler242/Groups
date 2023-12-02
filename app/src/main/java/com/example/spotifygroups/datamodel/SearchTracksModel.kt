package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class SearchTracksModel(
    @SerializedName("tracks") val tracks: SearchData = SearchData()
)

data class SearchData(
    @SerializedName("href") val href: String = "",
    @SerializedName("limit") val limit: Int = 0,
    @SerializedName("next") val next: String? = null,
    @SerializedName("offset") val offset: Int = 0,
    @SerializedName("previous") val previous: String? = null,
    @SerializedName("total") val total: Int = 0,
    @SerializedName("items") val items: List<Playable> = emptyList()
)