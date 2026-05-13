package com.example.clients.domain.model

data class ClientWithAddresses(
    val client: Client,
    val addresses: List<Address>
)
