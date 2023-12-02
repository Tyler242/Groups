package com.example.spotifygroups.viewmodel

import com.example.spotifygroups.uistatemodel.AppUiState
import com.example.spotifygroups.uistatemodel.View
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel {
    private var _view: View = View.HOME
    private val _uiState = MutableStateFlow(AppUiState())
    val uiState: StateFlow<AppUiState> = _uiState.asStateFlow()

    fun renderHomeView() {
        _uiState.value = AppUiState(View.HOME)
    }

    fun renderSessionView() {
        _uiState.value = AppUiState(View.SESSION)
    }
}

