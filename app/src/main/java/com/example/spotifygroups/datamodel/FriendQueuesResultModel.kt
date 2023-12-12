package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class FriendQueuesResultModel(
    @SerializedName("length") val length: Int = 0,
    @SerializedName("queues") val queues: List<FriendQueue> = emptyList()
)

data class FriendQueue(
    @SerializedName("_id") val id: String,
    @SerializedName("creatorId") val creatorId: String,
    @SerializedName("creatorName") val creatorName: String
)