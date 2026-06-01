package com.example.clients.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.clients.data.local.dao.ClientDao
import com.example.clients.data.local.dao.CompanyDao
import com.example.clients.data.local.entity.AddressEntity
import com.example.clients.data.local.entity.ClientEntity
import com.example.clients.data.local.entity.CompanyEntity

@Database(
    entities = [
        ClientEntity::class,
        AddressEntity::class,
        CompanyEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class ClientDatabase : RoomDatabase() {

    abstract fun clientDao(): ClientDao
    abstract fun companyDao(): CompanyDao
}
