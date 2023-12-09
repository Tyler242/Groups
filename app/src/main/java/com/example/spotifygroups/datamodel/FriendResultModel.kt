package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class FriendResultModel(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String? = null,
    @SerializedName("friends") val friends: List<Friend> = emptyList()
)