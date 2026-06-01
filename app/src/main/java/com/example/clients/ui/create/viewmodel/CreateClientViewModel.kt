package com.example.clients.ui.create.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.clients.R
import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client
import com.example.clients.domain.model.Company
import com.example.clients.domain.model.DomainError
import com.example.clients.domain.usecase.client.GetClientByIdUseCase
import com.example.clients.domain.usecase.client.InsertClientWithAddressesUseCase
import com.example.clients.domain.usecase.client.UpdateClientWithAddressesUseCase
import com.example.clients.domain.usecase.company.GetCompaniesUseCase
import com.example.clients.domain.usecase.company.InsertCompanyUseCase
import com.example.clients.ui.core.toUiMessageRes
import com.example.clients.ui.create.states.CreateClientEvent
import com.example.clients.ui.create.states.CreateClientFormState
import com.example.clients.ui.create.states.CreateClientUiState
import com.example.clients.ui.create.states.ValidationErrors
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateClientViewModel @Inject constructor(
    private val insertClient: InsertClientWithAddressesUseCase,
    private val updateClient: UpdateClientWithAddressesUseCase,
    private val getClientById: GetClientByIdUseCase,
    private val getCompanies: GetCompaniesUseCase,
    private val insertCompany: InsertCompanyUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<CreateClientUiState>(
        CreateClientUiState.Form(
            data = CreateClientFormState(
                addresses = listOf(Address(id = 0, fullAddress = "", clientId = 0))
            )
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<CreateClientEvent>(extraBufferCapacity = 1)
    val events = _events.asSharedFlow()

    init {
        loadCompanies()
    }

    private fun loadCompanies() {
        viewModelScope.launch {
            getCompanies().collect { companies ->
                _uiState.update { current ->
                    if (current is CreateClientUiState.Form) {
                        current.copy(data = current.data.copy(companies = companies))
                    } else current
                }
            }
        }
    }

    fun createCompany(name: String, logoUri: String) {
        viewModelScope.launch {
            val id = insertCompany(Company(name = name, logoUri = logoUri))
            updateClientField { copy(companyId = id) }
        }
    }

    // Actualizar cualquier campo del Client
    fun updateClientField(transform: Client.() -> Client) {
        updateForm {
            copy(client = client.transform())
        }
    }

    // Actualiza una dirección específica.
    fun updateAddress(index: Int, value: String) {
        updateForm {
            val updated = addresses.toMutableList()

            if (index < updated.size) {
                updated[index] = updated[index].copy(fullAddress = value)
            } else {
                while (updated.size <= index) {
                    updated.add(
                        Address(
                            id = 0,
                            fullAddress = if (updated.size == index) value else "",
                            clientId = client.id
                        )
                    )
                }
            }
            copy(addresses = updated)
        }
    }

    // Añade una nueva dirección vacía a la lista
    fun addAddress() {
        updateForm {
            val updated = addresses.toMutableList()
            updated.add(Address(id = 0, fullAddress = "", clientId = client.id))
            copy(addresses = updated)
        }
    }

    // Elimina una dirección por su índice
    fun removeAddress(index: Int) {
        updateForm {
            val updated = addresses.toMutableList()
            if (updated.size > 1) {
                updated.removeAt(index)
            }
            copy(addresses = updated)
        }
    }

    // Actualiza la foto del cliente.
    fun updatePhoto(uri: String) {
        updateClientField { copy(photoUri = uri) }
    }

    // Actualiza el formulario de manera genérica.
    private fun updateForm(
        transform: CreateClientFormState.() -> CreateClientFormState
    ) {
        _uiState.update { current ->
            if (current is CreateClientUiState.Form) {
                current.copy(
                    data = current.data.transform(),
                    errors = ValidationErrors()
                )
            } else current
        }
    }

    // Carga un cliente desde la base de datos y actualiza el estado.
    fun loadClient(id: Int) {
        viewModelScope.launch {
            _uiState.value = CreateClientUiState.Loading

            try {
                val result = getClientById(id).firstOrNull()

                if (result != null) {
                    _uiState.value = CreateClientUiState.Form(
                        data = CreateClientFormState(
                            client = result.client,
                            addresses = result.addresses.ifEmpty {
                                listOf(Address(id = 0, fullAddress = "", clientId = result.client.id))
                            },
                            isEditMode = true
                        )
                    )
                    loadCompanies() // Recargar compañías para asegurar que la lista esté presente
                } else {
                    _uiState.value = CreateClientUiState.Error(R.string.client_not_found)
                }
            } catch (e: DomainError) {
                _uiState.value = CreateClientUiState.Error(e.toUiMessageRes())
            }
        }
    }

    // Guardar cliente
    fun saveClient() {
        viewModelScope.launch {
            val currentFormState = _uiState.value as? CreateClientUiState.Form ?: return@launch
            val form = currentFormState.data

            val finalAddresses = form.addresses
                .filter { it.fullAddress.isNotBlank() }
                .map { it.copy(fullAddress = it.fullAddress.trim()) }

            val firstAddress = finalAddresses.firstOrNull()?.fullAddress.orEmpty()
            val errors = validate(client = form.client, address1 = firstAddress)

            if (errors.hasErrors()) {
                _uiState.update { current ->
                    (current as? CreateClientUiState.Form)?.copy(errors = errors) ?: current
                }
                if (errors.image != null) {
                    _events.emit(CreateClientEvent.ShowMessage(R.string.message_select_image))
                }
                return@launch
            }

            _uiState.value = CreateClientUiState.Loading

            try {
                if (form.isEditMode) {
                    updateClient(form.client, finalAddresses)
                    _events.emit(CreateClientEvent.Updated)
                } else {
                    insertClient(form.client, finalAddresses)
                    _events.emit(CreateClientEvent.Created)
                }
            } catch (e: DomainError) {
                _uiState.value = CreateClientUiState.Error(e.toUiMessageRes())
            }
        }
    }


    // Valida campos requeridos.
    private fun validate(client: Client, address1: String): ValidationErrors {
        return ValidationErrors(
            image = if (client.photoUri.isBlank()) R.string.required else null,
            name = if (client.name.isBlank()) R.string.required else null,
            lastname = if (client.lastname.isBlank()) R.string.required else null,
            company = if (client.companyId == null) R.string.required else null,
            email = if (client.email.isBlank()) R.string.required else null,
            phone = if (client.phone.isBlank()) R.string.required else null,
            address1 = if (address1.isBlank()) R.string.required else null
        )
    }

}
