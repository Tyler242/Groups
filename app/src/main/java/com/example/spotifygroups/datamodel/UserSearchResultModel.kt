package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class UserSearchResultModel(
    @SerializedName("length") val length: Int,
    @SerializedName("users") val users: List<UserModel> = emptyList()
)

data class UserModel(
    @SerializedName("_id") val id: String,
    @SerializedName("name") val name: String? = null
)