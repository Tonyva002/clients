package com.example.clients.domain.repository

import com.example.clients.domain.model.Company
import kotlinx.coroutines.flow.Flow

interface CompanyRepository {
    fun getCompanies(): Flow<List<Company>>
    suspend fun getCompanyById(id: Long): Company?
    suspend fun insertCompany(company: Company): Long
    suspend fun updateCompany(company: Company)
    suspend fun deleteCompany(company: Company)
}
