package com.example.spotifygroups.datamodel

import com.google.gson.annotations.SerializedName

data class QueueError(
    @SerializedName("participant") val participant: Boolean
)