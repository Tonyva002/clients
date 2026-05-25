package com.example.clients.ui.home.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.clients.R
import com.example.clients.domain.model.Client
import com.example.clients.ui.core.CustomOptionsDialog
import com.example.clients.ui.core.DialogOption
import com.example.clients.ui.home.components.ClientList
import com.example.clients.ui.home.components.dial
import com.example.clients.ui.home.states.HomeEvent
import com.example.clients.ui.home.states.HomeUiState
import com.example.clients.ui.home.viewmodel.HomeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var selectedClient by remember {
        mutableStateOf<Client?>(null)

    }

    // Eventos
    LaunchedEffect(Unit) {

        viewModel.events.collect { event ->

            when (event) {

                is HomeEvent.ShowMessage -> {

                    Toast.makeText(
                        context,
                        event.resId,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Clients") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("create/-1") }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is HomeUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                is HomeUiState.Success -> {
                    ClientList(
                        clients = state.clients,

                        onClick = { client ->
                            navController.navigate("create/${client.id}")
                        },

                        onLongClick = { client ->
                            selectedClient = client

                        }
                    )
                }

                is HomeUiState.Error -> {
                    Text(
                        text = stringResource(state.message),
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
        }
    }

    // Dialog
    selectedClient?.let { client ->
        CustomOptionsDialog(
            title = stringResource(R.string.options),
            message = stringResource(R.string.message_select_an_option),
            options = listOf(
                DialogOption(
                    text =  stringResource(R.string.delete),
                    onClick = {
                        selectedClient = null
                        viewModel.delete(client) },
                ),
                DialogOption(
                    text = stringResource(R.string.call),
                    onClick = {
                        selectedClient = null
                        dial(context = context, phone = client.phone) },
                )
            ),
            onDismiss = {
                selectedClient = null
            },

            )
    }
}





