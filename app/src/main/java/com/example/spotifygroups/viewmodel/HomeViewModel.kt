package com.example.spotifygroups.viewmodel

import androidx.lifecycle.ViewModel
import com.example.spotifygroups.uistatemodel.HomeActions
import com.example.spotifygroups.uistatemodel.HomeUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState : StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun update(action : HomeActions) {
        when(action) {
            HomeActions.GROUP -> {
                _uiState.value = HomeUiState(HomeActions.GROUP)
            }
            HomeActions.FRIENDS -> {
                _uiState.value = HomeUiState(HomeActions.FRIENDS)
            }
            HomeActions.NONE -> {
                _uiState.value = HomeUiState(HomeActions.NONE)
            }
        }
    }
}