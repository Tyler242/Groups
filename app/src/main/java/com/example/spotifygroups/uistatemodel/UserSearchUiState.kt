package com.example.spotifygroups.uistatemodel

import com.example.spotifygroups.datamodel.UserModel

data class UserSearchUiState(
    val query: String = "",
    val searchResults: List<UserModel> = emptyList()
)