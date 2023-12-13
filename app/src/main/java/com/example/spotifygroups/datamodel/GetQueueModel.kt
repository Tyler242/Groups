package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName
import com.spotify.protocol.types.Artist

data class GetQueueModel(
    @SerializedName("currently_playing") val currentPlaying: Playable? = null,
    @SerializedName("queue") val queue: List<Playable> = emptyList()
)

data class Playable(
    // tracks only
    @SerializedName("album") val album: Album? = null,
    @SerializedName("artists") val artists: List<Artist> = emptyList(),
    // both episodes and tracks
    @SerializedName("duration_ms") val duration: Long,
    @SerializedName("explicit") val explicit: Boolean,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("is_playable") val isPlayable: Boolean,
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String
)

data class Album(
    @SerializedName("album_type") val albumType: String,
    @SerializedName("total_tracks") val totalTracks: Int,
    @SerializedName("href") val href: String,
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String
)

data class Image(
    @SerializedName("url") val url: String,
    @SerializedName("height") val height: Int,
    @SerializedName("width") val width: Int,
)