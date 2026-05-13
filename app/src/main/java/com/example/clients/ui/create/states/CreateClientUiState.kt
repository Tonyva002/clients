package com.example.clients.ui.create.states

import com.example.clients.domain.model.Address
import com.example.clients.domain.model.Client

data class CreateClientFormState(
    val client: Client = Client(),
    val addresses: List<Address> = emptyList(),
    val isEditMode: Boolean = false

)

 data class ValidationErrors(

    val image: Int? = null,
    val name: Int? = null,
    val lastname: Int? = null,
    val company: Int? = null,
    val email: Int? = null,
    val phone: Int? = null,
    val address1: Int? = null

){
    fun hasErrors() =
        listOf(image, name, lastname, email, phone, address1).any {it != null}
}


sealed class CreateClientUiState {

    object Loading : CreateClientUiState()

    data class Form(
        val data: CreateClientFormState,
        val errors: ValidationErrors = ValidationErrors()
    ): CreateClientUiState()

    data class Error(val message: Int): CreateClientUiState()
}