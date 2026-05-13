package com.example.clients.domain.usecase.client


import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.repository.ClientRepository
import com.example.clients.domain.usecase.client.UpdateClientWithAddressesUseCase
import com.example.clients.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UpdateClientWithAddressesUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: ClientRepository = mockk(relaxed = false)
    private val useCase = UpdateClientWithAddressesUseCase(repository)

    @Test
    fun `should call repository with correct client and addresses`() = runTest {

        val client = Client(id = 1, name = "Tony")
        val addresses = listOf(
            Address(id = 1, fullAddress = "Street 1", clientId = 1),
            Address(id = 2, fullAddress = "Street 2", clientId = 1)
        )

        coEvery {
            repository.updateClientWithAddresses(client, addresses)
        } returns Unit

        useCase(client, addresses)

        coVerify(exactly = 1) {
            repository.updateClientWithAddresses(client, addresses)
        }
    }
}