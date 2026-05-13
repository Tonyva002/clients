package com.example.clients.data.repository

import android.database.sqlite.SQLiteException
import com.example.clients.data.local.dao.ClientDao
import com.example.clients.data.mapper.toDomain
import com.example.clients.data.mapper.toEntity
import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.model.ClientWithAddresses
import com.example.clients.domain.model.DomainError
import com.example.clients.domain.repository.ClientRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ClientRepositoryImpl @Inject constructor(
    private val clientDao: ClientDao
) : ClientRepository {

    // Obtener todos los clientes
    override fun getClients(): Flow<List<ClientWithAddresses>> {
        return clientDao.getClientsWithAddresses().map { list ->
            list.map { relation ->
                ClientWithAddresses(
                    client = relation.client.toDomain(),
                    addresses = relation.addresses.map { it.toDomain() }
                )
            }
        }
    }

    // Obtener cliente por id
    override fun getClientById(clientId: Int): Flow<ClientWithAddresses?> {
        return clientDao.getClientWithAddressesById(clientId).map { relation ->
            relation?.let {
                ClientWithAddresses(
                    client = it.client.toDomain(),
                    addresses = it.addresses.map { addr -> addr.toDomain() }
                )
            }
        }
    }

    // Buscar clientes por nombre, apellido o compañia
    override fun searchClients(query: String): Flow<List<ClientWithAddresses>> {
        return clientDao.searchClients(query).map { list ->
            list.map { relation ->
                ClientWithAddresses(
                    client = relation.client.toDomain(),
                    addresses = relation.addresses.map { it.toDomain() }
                )
            }
        }
    }

    override suspend fun insertClientWithAddresses(
        client: Client,
        addresses: List<Address>
    ) {

        try {

            clientDao.insertClientWithAddresses(
                client.toEntity(),
                addresses.map { it.toEntity() }
            )

        } catch (e: SQLiteException) {

            throw DomainError.Server()

        } catch (e: Exception) {

            throw DomainError.Unknown()
        }
    }

    override suspend fun updateClientWithAddresses(
        client: Client,
        addresses: List<Address>
    ) {

        try {

            clientDao.updateClientWithAddresses(
                client.toEntity(),
                addresses.map { it.toEntity() }
            )

        } catch (e: SQLiteException) {

            throw DomainError.Server()

        } catch (e: Exception) {

            throw DomainError.Unknown()
        }
    }

    override suspend fun deleteClient(client: Client) {

        try {

            clientDao.deleteClient(client.toEntity())

        } catch (e: SQLiteException) {

            throw DomainError.Server()

        } catch (e: Exception) {

            throw DomainError.Unknown()
        }
    }
}