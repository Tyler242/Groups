package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class Friend(
    @SerializedName("userId") val userId: String,
    @SerializedName("name") val name: String? = null
)