package com.example.clients.data.repository

import com.example.clients.data.local.dao.CompanyDao
import com.example.clients.data.mapper.toDomain
import com.example.clients.data.mapper.toEntity
import com.example.clients.domain.model.Company
import com.example.clients.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CompanyRepositoryImpl @Inject constructor(
    private val companyDao: CompanyDao
) : CompanyRepository {
    override fun getCompanies(): Flow<List<Company>> {
        return companyDao.getAllCompanies().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun getCompanyById(id: Long): Company? {
        return companyDao.getCompanyById(id)?.toDomain()
    }

    override suspend fun insertCompany(company: Company): Long {
        return companyDao.insertCompany(company.toEntity())
    }

    override suspend fun updateCompany(company: Company) {
        companyDao.updateCompany(company.toEntity())
    }

    override suspend fun deleteCompany(company: Company) {
        companyDao.deleteCompany(company.toEntity())
    }
}
