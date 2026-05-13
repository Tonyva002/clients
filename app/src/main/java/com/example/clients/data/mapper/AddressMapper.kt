package com.example.clients.data.mapper

import com.example.clients.data.local.entity.AddressEntity
import com.example.clients.domain.model.Address


fun AddressEntity.toDomain() = Address(
    id = id,
    fullAddress = fullAddress,
    clientId = clientId
)


fun Address.toEntity() = AddressEntity(
    id = id,
    fullAddress = fullAddress,
    clientId = clientId
)