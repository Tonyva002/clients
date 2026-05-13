package com.example.clients.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clients.data.local.dao.ClientDao
import com.example.clients.data.local.entity.AddressEntity
import com.example.clients.data.local.entity.ClientEntity

@Database(
    entities = [
        ClientEntity::class,
        AddressEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class ClientDatabase : RoomDatabase() {

    abstract fun clientDao() : ClientDao
}