package com.example.clients.ui.home.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.example.clients.domain.model.Client

@Composable
fun ClientList(
    clients: List<Client>,
    onClick: (Client) -> Unit,
    onLongClick: (Client) -> Unit
) {

    LazyColumn(
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        items(clients) { client ->

            ClientItem(
                client = client,
                onClick = {
                    onClick(client)
                },
                onLongClick = {
                    onLongClick(client)
                }
            )
        }
    }
}