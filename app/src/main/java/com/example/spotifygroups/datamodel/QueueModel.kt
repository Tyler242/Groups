package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class QueueResultModel(
    @SerializedName("_id") val id: String,
    @SerializedName("queue") val queue: List<QueueItem> = emptyList(),
    @SerializedName("lengthOfQueue") val length: Int = 0,
    @SerializedName("creatorId") val creatorId: String,
    @SerializedName("participants") val participants: List<Friend> = emptyList(),
    @SerializedName("isPaused") val isPaused: Boolean = true,
    @SerializedName("currentTrack") val currentTrack: QueueItem = QueueItem(),
    @SerializedName("positionMs") val positionMs: Long? = null
)

data class QueueItem(
    @SerializedName("playable") val playable: QPlayable? = null,
    @SerializedName("next") val next: QPlayable? = null
)

data class QPlayable(
    @SerializedName("image") val image: Image? = null,
    @SerializedName("artists") val artists: List<String> = emptyList(),
    @SerializedName("duration") val duration: Long = 0,
    @SerializedName("name") val name: String,
    @SerializedName("uri") val uri: String,
    @SerializedName("spotifyId") val spotifyId: String
)

data class CreateQueueRequestModel(
    val userId: String
)

data class QueueAddRequestModel(
    val image: Image? = null,
    val artists: List<String> = emptyList(),
    val duration: Long,
    val name: String,
    val uri: String,
    val spotifyId: String
)