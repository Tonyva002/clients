package com.example.clients.domain.usecase.client

import com.example.clients.domain.model.Client
import com.example.clients.domain.repository.ClientRepository
import javax.inject.Inject

class DeleteClientUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(client: Client) {
        repository.deleteClient(client)
    }
}