package com.example.clients.ui.create.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
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
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.clients.R
import com.example.clients.ui.common.CustomOptionsDialog
import com.example.clients.ui.common.DialogOption
import com.example.clients.ui.create.components.CreateClientContent
import com.example.clients.ui.create.states.CreateClientEvent
import com.example.clients.ui.create.states.CreateClientUiState
import com.example.clients.ui.create.viewmodel.CreateClientViewModel
import java.io.File

@Composable
fun CreateClientScreen(
    navController: NavController,
    id: Int = -1,
    viewModel: CreateClientViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var showImageDialog by remember { mutableStateOf(false)}


    // Uri temporal para la camara
    val camaraImageUri = remember {
        val file = File.createTempFile(
            "client_image",
            ".jpg",
            context.cacheDir
        )
        FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    }

    // Lanzar camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->

        if (success) {
            viewModel.updatePhoto(
                camaraImageUri.toString()
            )
        }

    }

    // Lanzar la galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->

        if (uri != null) {

            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            viewModel.updatePhoto(
                uri.toString()
            )
        }
    }


    // Load client
    LaunchedEffect(id) {

        if (id != -1) {
            viewModel.loadClient(id)
        }
    }

    // Events
    LaunchedEffect(Unit) {

        viewModel.events.collect { event ->

            when (event) {

                is CreateClientEvent.Created -> {

                    Toast.makeText(
                        context,
                        R.string.message_created_client_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.popBackStack()
                }

                is CreateClientEvent.Updated -> {

                    Toast.makeText(
                        context,
                        R.string.message_updated_client_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    navController.popBackStack()
                }

                is CreateClientEvent.ShowMessage -> {

                    Toast.makeText(
                        context,
                        event.resId,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    when (val state = uiState) {

        is CreateClientUiState.Loading -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                CircularProgressIndicator()
            }
        }

        is CreateClientUiState.Form -> {

            val form = state.data

            CreateClientContent(

                form = form,

                errors = state.errors,

                onSelectImage = {
                    showImageDialog = true
                },

                onNameChange = {

                    viewModel.updateClientField {
                        copy(name = it)
                    }
                },

                onLastNameChange = {

                    viewModel.updateClientField {
                        copy(lastname = it)
                    }
                },

                onCompanyChange = {

                    viewModel.updateClientField {
                        copy(company = it)
                    }
                },

                onEmailChange = {

                    viewModel.updateClientField {
                        copy(email = it)
                    }
                },

                onPhoneChange = {

                    viewModel.updateClientField {
                        copy(phone = it)
                    }
                },

                onAddress1Change = {

                    viewModel.updateAddress(
                        index = 0,
                        value = it
                    )
                },

                onAddress2Change = {

                    viewModel.updateAddress(
                        index = 1,
                        value = it
                    )
                },

                onSave = {

                    viewModel.saveClient()
                }
            )
        }

        is CreateClientUiState.Error -> {

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {

                Text(stringResource(state.message))
            }
        }
    }

    if (showImageDialog){
        CustomOptionsDialog(
            title = stringResource(R.string.message_select_an_option),
            options = listOf(
                DialogOption(
                    text = stringResource(R.string.camera),
                    onClick = {
                        showImageDialog = false
                        cameraLauncher.launch(camaraImageUri)
                    }

                ),

                DialogOption(
                    text = stringResource(R.string.gallery),
                    onClick = {
                        showImageDialog = false
                        galleryLauncher.launch(arrayOf("image/*"))
                    }
                )
            ),
            onDismiss = {
                showImageDialog = false
            }
        )
    }
}