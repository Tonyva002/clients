package com.pangea.clients.ui.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pangea.clients.R
import com.pangea.clients.domain.model.Address
import com.pangea.clients.domain.model.Client
import com.pangea.clients.domain.model.Company
import com.pangea.clients.domain.model.DomainError
import com.pangea.clients.domain.usecase.client.GetClientByIdUseCase
import com.pangea.clients.domain.usecase.client.InsertClientWithAddressesUseCase
import com.pangea.clients.domain.usecase.client.UpdateClientWithAddressesUseCase
import com.pangea.clients.domain.usecase.company.GetCompaniesUseCase
import com.pangea.clients.domain.usecase.company.InsertCompanyUseCase
import com.pangea.clients.ui.core.toUiMessageRes
import com.pangea.clients.ui.create.states.CreateClientEvent
import com.pangea.clients.ui.create.states.CreateClientFormState
import com.pangea.clients.ui.create.states.CreateClientUiState
import com.pangea.clients.ui.create.states.ValidationErrors
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

    private val _uiState = MutableStateFlow<CreateClientUiState>(   // Aquí vive el estado de la UI.
        CreateClientUiState.Form( // Estado inicial, Cuando la pantalla abre, tiene:
            data = CreateClientFormState( // Cliente vacío
                addresses = listOf(  // Dirección vacía
                    Address(
                        id = 0,
                        fullAddress = "",
                        clientId = 0
                    )
                )
            )
        )
    )
    val uiState = _uiState.asStateFlow() // Exposición del estado. La UI puede leerlo, pero no modificarlo.

    private val _events =
        MutableSharedFlow<CreateClientEvent>(replay = 1) // Eventos, son acciones de una sola vez. mostrar Toast, cerrar pantalla, cliente creado,  cliente actualizado

    val events = _events.asSharedFlow() // Exposición, la UI observa eventos.

    init { // Se ejecuta automáticamente cuando se crea el ViewModel.
        loadCompanies()
    }


    // Cargar compañías
    private fun loadCompanies() {
        viewModelScope.launch { // Lanza una corrutina ligada al ciclo de vida del ViewModel.
            getCompanies().collect { companies -> // Obtener flujo de compañías. Escucha continuamente las compañías.
                _uiState.update { current -> // Actualizar UI
                    if (current is CreateClientUiState.Form) { // Si estamos en estado Form:
                        current.copy(data = current.data.copy(companies = companies)) // Actualiza la lista de compañías.
                    } else current
                }
            }
        }
    }

    // Crear compañía
    fun createCompany(name: String, logoUri: String) {
        viewModelScope.launch {
            val id = insertCompany(Company(name = name, logoUri = logoUri)) // Guarda la compañía.
            updateClientField { copy(companyId = id) } // El cliente queda asociado a la compañía recién creada.
        }
    }

    // Actualizar cualquier campo del client
    fun updateClientField(transform: Client.() -> Client) {
        updateForm {
            copy(client = client.transform()) // Actualiza solo el campo necesario.
        }
    }

    // Actualizar dirección.
    fun updateAddress(index: Int, value: String) {
        updateForm {
            val updated = addresses.toMutableList() // Copia mutable, porque las listas son inmutables.

            if (index < updated.size) { // Si existe
                updated[index] = updated[index].copy(fullAddress = value)
            } else { // Si no existe
                while (updated.size <= index) {
                    updated.add( // Agrega direcciones vacías.
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

    // Agregar dirección
    fun addAddress() {
        updateForm {
            val updated = addresses.toMutableList()
            updated.add( // Crea y la agrega.
                Address(
                    id = 0,
                    fullAddress = "",
                    clientId = client.id
                )
            )
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
                    errors = ValidationErrors()  // Cada vez que se actualiza, limpia errores anteriores.
                )
            } else current
        }
    }

    // Cargar cliente por id
    fun loadClient(id: Int) {
        viewModelScope.launch {
            _uiState.value = CreateClientUiState.Loading // Mostrar Loading

            try {
                val result = getClientById(id).firstOrNull() // Buscar cliente

                if (result != null) {
                    _uiState.value = CreateClientUiState.Form( // Actualizar formulario
                        data = CreateClientFormState(
                            client = result.client,
                            addresses = result.addresses.ifEmpty {
                                listOf(
                                    Address(
                                        id = 0,
                                        fullAddress = "",
                                        clientId = result.client.id
                                    )
                                )
                            },
                            isEditMode = true  // Esto activa el modo edición.
                        )
                    )
                    loadCompanies() // Recargar compañías para asegurar que la lista esté presente
                } else {
                    _uiState.value = CreateClientUiState.Error(R.string.client_not_found) // Si no existe, lanza el error
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
            val form = currentFormState.data  // Obtener formulario actual

            val finalAddresses = form.addresses  // Limpiar direcciones
                .filter { it.fullAddress.isNotBlank() } // Filtra vacías
                .map { it.copy(fullAddress = it.fullAddress.trim()) } // Elimina espacios.

            val firstAddress = finalAddresses.firstOrNull()?.fullAddress.orEmpty()
            val errors = validate(client = form.client, address1 = firstAddress) // Validar

            if (errors.hasErrors()) { // Si hay errores
                _uiState.update { current ->
                    (current as? CreateClientUiState.Form)?.copy(errors = errors) ?: current  //Actualiza, para mostrarlos en pantalla.
                }
                if (errors.image != null) { // Si falta imagen
                    _events.emit(CreateClientEvent.ShowMessage(R.string.message_select_image)) // Muestra mensaje.
                }
                return@launch   // Y termina
            }

            _uiState.value = CreateClientUiState.Loading  // Mostrar Loading mientras guarda.

            try {
                if (form.isEditMode) { // Si está editando
                    updateClient(form.client, finalAddresses)  // Actualiza
                    _events.emit(CreateClientEvent.Updated)
                } else { // Si es nuevo
                    insertClient(form.client, finalAddresses) // Crear
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