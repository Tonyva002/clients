package com.example.clients.data.di

import android.content.Context
import androidx.room.Room
import com.example.clients.data.local.dao.ClientDao
import com.example.clients.data.local.dao.CompanyDao
import com.example.clients.data.local.database.ClientDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): ClientDatabase = Room.databaseBuilder(
        context,
        ClientDatabase::class.java,
        "client_management_db"
    )
        .fallbackToDestructiveMigration(true)
        .build()


    @Provides
    fun provideClientDao(db: ClientDatabase): ClientDao = db.clientDao()

    @Provides
    fun provideCompanyDao(db: ClientDatabase): CompanyDao = db.companyDao()
}
