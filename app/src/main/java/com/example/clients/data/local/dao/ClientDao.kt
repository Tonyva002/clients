package com.example.clients.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.clients.data.local.entity.AddressEntity
import com.example.clients.data.local.entity.ClientEntity
import com.example.clients.data.local.relation.ClientWithAddresses
import kotlinx.coroutines.flow.Flow

@Dao
interface ClientDao {

    // ====================
    // INSERT
    // ====================

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClient(client: ClientEntity): Long


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAddresses(addresses: List<AddressEntity>)



    // ====================
    // UPDATE
    // ====================

    @Update
    suspend fun updateClient(client: ClientEntity)



    // ====================
    // DELETE
    // ====================

    @Query("DELETE FROM addresses WHERE clientId = :clientId")
    suspend fun deleteAddressesByClientId(clientId: Long)

    // Eliminar cliente (borra direcciones por CASCADE)
    @Delete
    suspend fun deleteClient(client: ClientEntity)




    // ====================
    // QUERIES
    // ====================

    // Obtener todos los clientes con sus direcciones
    @Transaction
    @Query("SELECT * FROM clients")
    fun getClientsWithAddresses(): Flow<List<ClientWithAddresses>>

    // Obtener un cliente con sus direcciones
    @Transaction
    @Query("SELECT * FROM clients WHERE id = :clientId")
    fun getClientWithAddressesById(clientId: Int): Flow<ClientWithAddresses?>

    // Buscar por nombre, apellido o compañía
    @Transaction
    @Query(
        """SELECT clients.* FROM clients
            LEFT JOIN companies ON clients.companyId = companies.id
            WHERE LOWER(clients.name) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(clients.lastname) LIKE '%' || LOWER(:query) || '%'
            OR LOWER(companies.name) LIKE '%' || LOWER(:query) || '%'
        """
    )
    fun searchClients(query: String): Flow<List<ClientWithAddresses>>



    // ====================
    // TRANSACTIONS
    // ====================

    // Insertar cliente con direcciones
    @Transaction
    suspend fun insertClientWithAddresses(
        client: ClientEntity,
        addresses: List<AddressEntity>
    ) {
        val clientId = insertClient(client)

        val addressesWithClientId = addresses.map {
            it.copy(clientId = clientId)
        }

        insertAddresses(addressesWithClientId)
    }


    // Actualizar cliente con direcciones
    @Transaction
    suspend fun updateClientWithAddresses(
        client: ClientEntity,
        addresses: List<AddressEntity>
    ) {
        // 1. Actualizar cliente
        updateClient(client)

        // 2. Eliminar direcciones anteriores
        deleteAddressesByClientId(client.id)

        // 3. Insertar nuevas direcciones
        val updateAddresses = addresses.map {
            it.copy(clientId = client.id)
        }
        insertAddresses(updateAddresses)
    }


}
