package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

class SearchUsersResultModel(
    @SerializedName("length") val length: Int,
    @SerializedName("users") val users: List<Friend> = emptyList()
)