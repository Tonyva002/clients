package com.example.clients.data.mapper

import com.example.clients.data.local.entity.ClientEntity
import com.example.clients.domain.model.Client


fun ClientEntity.toDomain() = Client(
    id = id,
    name = name,
    lastname = lastname,
    companyId = companyId,
    email = email,
    phone = phone,
    photoUri = photoUri
)

fun Client.toEntity() = ClientEntity(
    id = id,
    name = name,
    lastname = lastname,
    companyId = companyId,
    email = email,
    phone = phone,
    photoUri = photoUri
)
