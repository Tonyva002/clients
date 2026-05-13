package com.example.clients.domain.usecase.client

import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.repository.ClientRepository
import javax.inject.Inject

class InsertClientWithAddressesUseCase @Inject constructor(
    private val repository: ClientRepository
) {
    suspend operator fun invoke(
        client: Client,
        addresses: List<Address>
    ) {
        return repository.insertClientWithAddresses(client, addresses)

    }
}