package com.example.clients.domain.model

data class Client(
    val id: Long = 0,
    val name: String = "",
    val lastname: String = "",
    val companyId: Long? = null,
    val email: String = "",
    val phone: String = "",
    val photoUri: String = ""
)
