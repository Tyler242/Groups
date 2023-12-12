package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class NameResult(
    @SerializedName("name") val name: String? = null
) {
}