package com.example.clients.domain.usecase.client

import com.example.clients.domain.model.Client
import com.example.clients.domain.model.ClientWithAddresses
import com.example.clients.domain.repository.ClientRepository
import com.example.clients.utils.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class GetClientByIdUseCaseTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val repository: ClientRepository = mockk()

    private val useCase = GetClientByIdUseCase(repository)

    @Test
    fun `invoke should return client when found`() = runTest {

        // Arrange
        val clientId = 1

        val expectedClient = ClientWithAddresses(
            client = Client(
                id = clientId.toLong(),
                name = "Tony"
            ),
            addresses = emptyList()
        )

        every {
            repository.getClientById(clientId)
        } returns flowOf(expectedClient)

        // Act
        val result = useCase(clientId).first()

        // Assert
        assertEquals(expectedClient, result)
    }

    @Test
    fun `invoke should return null when client not found`() = runTest {

        // Arrange
        val clientId = 99

        every {
            repository.getClientById(clientId)
        } returns flowOf(null)

        // Act
        val result = useCase(clientId).first()

        // Assert
        assertNull(result)
    }
}