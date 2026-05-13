package com.example.clients.domain.usecase.client

import com.example.clients.domain.model.ClientWithAddresses
import com.example.clients.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchClientsUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    operator fun invoke(query: String): Flow<List<ClientWithAddresses>> {
        return repository.searchClients(query)
    }
}