package com.example.clients.domain.usecase.client

import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.repository.ClientRepository
import com.example.clients.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class InsertClientWithAddressesUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: ClientRepository = mockk()
    private val useCase = InsertClientWithAddressesUseCase(repository)

    @Test
    fun `invoke should call repository to insert client and addresses`() = runTest {
        // Arrange
        val client = Client(id = 1, name = "Tony")
        val addresses = listOf(
            Address(id = 0, fullAddress = "Calle 1", clientId = 1),
            Address(id = 0, fullAddress = "Calle 2", clientId = 1)
        )

        // Configuramos el mock para devolver Unit (que es lo que devuelve una función suspend sin retorno)
        coEvery { repository.insertClientWithAddresses(client, addresses) } returns Unit

        // Act
        useCase(client, addresses)

        // Assert
        coVerify(exactly = 1) { repository.insertClientWithAddresses(client, addresses) }
    }
}