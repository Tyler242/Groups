package com.example.spotifygroups.uistatemodel

data class HomeUiState(val action: HomeActions = HomeActions.NONE)

enum class HomeActions {
    GROUP, FRIENDS, NONE
}