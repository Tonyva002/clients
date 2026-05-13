package com.example.clients.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "clients")
data class ClientEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val lastname: String,
    val company: String,
    val email: String,
    val phone: String,
    val photoUri: String

)