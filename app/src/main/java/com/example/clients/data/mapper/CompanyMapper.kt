package com.example.clients.data.mapper

import com.example.clients.data.local.entity.CompanyEntity
import com.example.clients.domain.model.Company

fun CompanyEntity.toDomain() = Company(
    id = id,
    name = name,
    rnc = rnc,
    logoUri = logoUri
)

fun Company.toEntity() = CompanyEntity(
    id = id,
    name = name,
    rnc = rnc,
    logoUri = logoUri
)
