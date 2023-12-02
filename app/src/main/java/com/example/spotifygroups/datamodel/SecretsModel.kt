package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class SecretsModel(
    @SerializedName("clientId") val clientId: String,
    @SerializedName("redirectUri") val redirectUri: String
)