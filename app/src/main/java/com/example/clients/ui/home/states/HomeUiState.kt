package com.example.clients.ui.home.states

import com.example.clients.domain.model.ClientWithAddresses

sealed class HomeUiState {

    object Loading : HomeUiState()

    data class Success(val clients: List<ClientWithAddresses>) : HomeUiState()

    data class Error(val message: Int) : HomeUiState()
}
