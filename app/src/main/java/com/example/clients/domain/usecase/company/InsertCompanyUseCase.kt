package com.example.clients.domain.usecase.company

import com.example.clients.domain.model.Company
import com.example.clients.domain.repository.CompanyRepository
import javax.inject.Inject

class InsertCompanyUseCase @Inject constructor(
    private val repository: CompanyRepository
) {
    suspend operator fun invoke(company: Company): Long {
        return repository.insertCompany(company)
    }
}
