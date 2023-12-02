package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class AuthResultModel(
    @SerializedName("auth_token") val authTokenModel: AuthTokenModel,
    @SerializedName("id") val userId: String,
    @SerializedName("spotify_uuid") val spotifyUuid: String,
    @SerializedName("email") val email: String
)

data class AuthTokenModel(
    @SerializedName("token") val token: String,
    @SerializedName("expires_in") val expireIn: Number
)