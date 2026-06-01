package com.example.clients.domain.usecase.company

import com.example.clients.domain.model.Company
import com.example.clients.domain.repository.CompanyRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCompaniesUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    operator fun invoke(): Flow<List<Company>> {
        return repository.getCompanies()
    }
}
