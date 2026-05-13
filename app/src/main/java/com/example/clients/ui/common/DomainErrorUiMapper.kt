package com.example.clients.ui.common

import com.example.clients.R
import com.example.clients.domain.model.DomainError


fun DomainError.toUiMessageRes(): Int = when (this) {
    is DomainError.Network -> R.string.message_not_network
    is DomainError.NotFound -> R.string.message_not_found_client
    is DomainError.Server -> R.string.message_server_not_responding
    is DomainError.Unknown -> R.string.message_error
}