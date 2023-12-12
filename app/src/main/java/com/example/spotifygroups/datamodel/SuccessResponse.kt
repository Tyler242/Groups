package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class SuccessResponse(
    @SerializedName("success") val success: Boolean = false
)