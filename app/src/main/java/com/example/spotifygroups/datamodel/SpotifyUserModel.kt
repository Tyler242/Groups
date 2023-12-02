package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class SpotifyUserModel(
    @SerializedName("display_name") val name: String,
    @SerializedName("id") val spotifyUuid: String,
    @SerializedName("email") val email: String,
    @SerializedName("product") var product: String? = null
)