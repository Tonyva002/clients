package com.example.clients.ui.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clients.domain.model.Client
import com.example.clients.domain.model.DomainError
import com.example.clients.domain.usecase.client.DeleteClientUseCase
import com.example.clients.domain.usecase.client.GetClientsUseCase
import com.example.clients.ui.core.toUiMessageRes
import com.example.clients.ui.home.states.HomeEvent
import com.example.clients.ui.home.states.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getClients: GetClientsUseCase,
    private val deleteClient: DeleteClientUseCase,
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<HomeUiState>(
            HomeUiState.Loading
        )

    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<HomeEvent>()

    val events = _events.asSharedFlow()

    private var loadJob: Job? = null

    init {
        loadClients()
    }

    fun loadClients() {

        loadJob?.cancel()

        loadJob = viewModelScope.launch {

            _uiState.value = HomeUiState.Loading

            try {

                getClients().collect { clients ->

                    _uiState.value = HomeUiState.Success(clients)
                }

            } catch (e: DomainError) {

                _uiState.value = HomeUiState.Error(
                    e.toUiMessageRes()
                )

                _events.emit(
                    HomeEvent.ShowMessage(
                        e.toUiMessageRes()
                    )
                )
            }
        }
    }

    fun delete(client: Client) {

        viewModelScope.launch {

            try {

                deleteClient(client)

            } catch (e: DomainError) {

                _events.emit(
                    HomeEvent.ShowMessage(
                        e.toUiMessageRes()
                    )
                )
            }
        }
    }
}