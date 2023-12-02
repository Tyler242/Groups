package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class QueueResultModel(
    @SerializedName("queue") val queue: List<String> = emptyList(),
    @SerializedName("_id") val _id: String,
    @SerializedName("creatorId") val creatorId: String,
    @SerializedName("participantIds") val participantIds: List<String> = emptyList()
)

data class CreateQueueRequestModel(
    val userId: String
)

data class QueueAddRequestModel(
    val trackId: String
)