package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class QueueResultModel(
    @SerializedName("queue") val queue: List<String> = emptyList(),
    @SerializedName("_id") val _id: String,
    @SerializedName("creatorId") val creatorId: String,
    @SerializedName("participantIds") val participantIds: List<String> = emptyList(),
    @SerializedName("isPaused") val isPaused: Boolean = true,
    @SerializedName("currentTrack") val currentTrack: String? = null,
    @SerializedName("positionMs") val positionMs: Long? = null

)

data class CreateQueueRequestModel(
    val userId: String
)

data class QueueAddRequestModel(
    val trackId: String
)