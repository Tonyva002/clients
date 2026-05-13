package com.example.clients.domain.repository

import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.model.ClientWithAddresses
import kotlinx.coroutines.flow.Flow

interface ClientRepository {

    fun getClients(): Flow<List<ClientWithAddresses>>

    fun getClientById(clientId: Int): Flow<ClientWithAddresses?>

    fun searchClients(query: String): Flow<List<ClientWithAddresses>>

    suspend fun insertClientWithAddresses(client: Client, addresses: List<Address>)

    suspend fun updateClientWithAddresses(client: Client, addresses: List<Address>)

    suspend fun deleteClient(client: Client)
}