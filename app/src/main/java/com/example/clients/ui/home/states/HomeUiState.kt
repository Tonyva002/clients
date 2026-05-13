package com.example.clients.ui.home.states

import com.example.clients.domain.model.Client

sealed class HomeUiState {

    object Loading : HomeUiState()

    data class  Success(val clients: List<Client>) : HomeUiState()

    data class Error(val message: Int) : HomeUiState()
}