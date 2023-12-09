package com.example.spotifygroups.uistatemodel

data class AppUiState(val view: View = View.HOME)

enum class View {
    HOME, SESSION, FRIEND
}