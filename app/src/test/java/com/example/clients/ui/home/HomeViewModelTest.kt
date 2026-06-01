package com.example.clients.ui.home

import com.example.clients.domain.model.Client
import com.example.clients.domain.model.ClientWithAddresses
import com.example.clients.domain.model.DomainError
import com.example.clients.domain.usecase.client.DeleteClientUseCase
import com.example.clients.domain.usecase.client.GetClientsUseCase
import com.example.clients.ui.home.states.HomeEvent
import com.example.clients.ui.home.states.HomeUiState
import com.example.clients.ui.home.viewmodel.HomeViewModel
import com.example.clients.utils.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getClients: GetClientsUseCase = mockk()
    private val deleteClient: DeleteClientUseCase = mockk()

    @Test
    fun `should load clients successfully`() = runTest {

        // Arrange
        val clients = listOf(
            ClientWithAddresses(
                client = Client(
                    id = 1,
                    name = "Tony"
                ),
                addresses = emptyList()
            )
        )

        coEvery {
            getClients()
        } returns flowOf(clients)

        // Act
        val viewModel = HomeViewModel(
            getClients,
            deleteClient
        )

        advanceUntilIdle()

        // Assert
        val state = viewModel.uiState.value

        assertTrue(state is HomeUiState.Success)

        state as HomeUiState.Success

        assertEquals(1, state.clients.size)
        assertEquals("Tony", state.clients[0].client.name)
    }

    @Test
    fun `should emit error state and event when loading fails`() = runTest {

        coEvery { getClients() } returns flow {
            throw DomainError.Unknown()
        }

        val viewModel = HomeViewModel(getClients, deleteClient)

        val eventList = mutableListOf<HomeEvent>()

        val job = launch {
            viewModel.events.toList(eventList)
        }

        viewModel.loadClients()

        advanceUntilIdle()

        assertTrue(viewModel.uiState.value is HomeUiState.Error)

        assertTrue(
            eventList.any { it is HomeEvent.ShowMessage }
        )

        job.cancel()
    }

    @Test
    fun `should delete client successfully`() = runTest {

        // Arrange
        val client = Client(
            id = 1,
            name = "Tony"
        )

        coEvery {
            getClients()
        } returns flowOf(emptyList())

        coEvery {
            deleteClient(client)
        } returns Unit

        val viewModel = HomeViewModel(
            getClients,
            deleteClient
        )

        // Act
        viewModel.delete(client)

        advanceUntilIdle()

        // Assert
        coVerify(exactly = 1) {
            deleteClient(client)
        }
    }

    @Test
    fun `should emit event when delete fails`() = runTest {

        // Arrange
        val client = Client(
            id = 1,
            name = "Tony"
        )

        coEvery {
            getClients()
        } returns flowOf(emptyList())

        coEvery {
            deleteClient(client)
        } throws DomainError.Unknown()

        val viewModel = HomeViewModel(
            getClients,
            deleteClient
        )

        val events = mutableListOf<HomeEvent>()

        val job = launch {
            viewModel.events.toList(events)
        }

        // Act
        viewModel.delete(client)

        advanceUntilIdle()

        // Assert
        assertTrue(
            events.any { it is HomeEvent.ShowMessage }
        )

        job.cancel()
    }
}
