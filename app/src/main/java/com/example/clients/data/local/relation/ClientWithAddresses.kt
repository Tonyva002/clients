package com.example.clients.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.clients.data.local.entity.AddressEntity
import com.example.clients.data.local.entity.ClientEntity
import com.example.clients.data.local.entity.CompanyEntity


data class ClientWithAddresses(
    @Embedded val client: ClientEntity,

    @Relation(
        parentColumn = "id",
        entityColumn = "clientId"
    )
    val addresses: List<AddressEntity>,

    @Relation(
        parentColumn = "companyId",
        entityColumn = "id"
    )
    val company: CompanyEntity? = null
)
