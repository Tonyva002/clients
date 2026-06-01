package com.example.clients.ui.create.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.core.content.FileProvider
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.clients.R
import com.example.clients.domain.model.Client
import com.example.clients.ui.core.CustomOptionsDialog
import com.example.clients.ui.core.DialogOption
import com.example.clients.ui.create.components.ClientForm
import com.example.clients.ui.create.components.NewCompanyDialog
import com.example.clients.ui.create.states.CreateClientEvent
import com.example.clients.ui.create.states.CreateClientUiState
import com.example.clients.ui.create.viewmodel.CreateClientViewModel
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateClientScreen(
    navController: NavController,
    id: Int = -1,
    viewModel: CreateClientViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current

    var showImageDialog by remember { mutableStateOf(false) }

    var showNewCompanyDialog by remember { mutableStateOf(false) }

    var currentCameraUri by remember { mutableStateOf<Uri?>(null) }



    // Lanzar camara
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && currentCameraUri != null) {
            viewModel.updatePhoto(currentCameraUri.toString())
        }
    }

    // Lanzar galeria
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(
                    it,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                viewModel.updatePhoto(it.toString())
            }catch (e: SecurityException) {

            }

        }
    }

    // Cargar datos si es edición
    LaunchedEffect(id) {

        if (id != -1) {
            viewModel.loadClient(id)
        }
    }

    // Control de eventos únicos
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

    Scaffold(
        topBar = {

            val title = when (val state = uiState) {
                is CreateClientUiState.Form -> {
                    if (state.data.isEditMode)
                        stringResource(R.string.update_client)
                    else
                        stringResource(R.string.create_client)
                }
                else -> ""
            }

            TopAppBar(
                title = {
                    Text(title)
                },

                navigationIcon = {

                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            )
        }

    ) { padding ->
        Content(
            padding = padding,
            uiState = uiState,
            showImageDialog = showImageDialog,
            onShowImageDialogChange = { showImageDialog = it },
            onLaunchCamera = {
                val uri = generateTempImageUri(context)
                currentCameraUri = uri
                cameraLauncher.launch(uri)
            },
            onLaunchGallery = {
                galleryLauncher.launch(arrayOf("image/*"))
            },
            onFieldChange = { updateBlock -> viewModel.updateClientField(updateBlock) },
            onAddressChange = { index, value -> viewModel.updateAddress(index, value) },
            onAddAddress = { viewModel.addAddress() },
            onRemoveAddress = { index -> viewModel.removeAddress(index) },
            onAddCompany = { showNewCompanyDialog = true },
            onSave = { viewModel.saveClient() }
        )
    }

    if (showNewCompanyDialog) {
        NewCompanyDialog(
            onDismiss = { showNewCompanyDialog = false },
            onConfirm = { name, logoUri -> viewModel.createCompany(name, logoUri) }
        )
    }
}

// Uri temporal para la camara
private fun generateTempImageUri(context: Context): Uri {
    val file = File.createTempFile("client_image", ".jpg", context.cacheDir
    )
   return FileProvider.getUriForFile(context, "${context.packageName}.provider", file)
}

@Composable
private fun Content(
    padding: PaddingValues,
    uiState: CreateClientUiState,
    showImageDialog: Boolean,
    onShowImageDialogChange: (Boolean) -> Unit,
    onLaunchCamera: () -> Unit,
    onLaunchGallery: () -> Unit,
    onFieldChange: (Client.() -> Client) -> Unit,
    onAddressChange: (Int, String) -> Unit,
    onAddAddress: () -> Unit,
    onRemoveAddress: (Int) -> Unit,
    onAddCompany: () -> Unit,
    onSave: () -> Unit

) {
    // Memorizar las lambdas evita recomposiciones innecesarias en cascada sobre ClientForm
    val onNameChangeStable = remember(onFieldChange) { { text: String -> onFieldChange { copy(name = text) } } }
    val onLastNameChangeStable = remember(onFieldChange) { { text: String -> onFieldChange { copy(lastname = text) } } }
    val onCompanyChangeStable = remember(onFieldChange) { { id: Long -> onFieldChange { copy(companyId = id) } } }
    val onEmailChangeStable = remember(onFieldChange) { { text: String -> onFieldChange { copy(email = text) } } }
    val onPhoneChangeStable = remember(onFieldChange) { { text: String -> onFieldChange { copy(phone = text) } } }

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
            ClientForm(
                padding = padding,
                form = state.data,
                errors = state.errors,
                onSelectImage = { onShowImageDialogChange(true) },
                onNameChange = onNameChangeStable,
                onLastNameChange = onLastNameChangeStable,
                onCompanyChange = onCompanyChangeStable,
                onEmailChange = onEmailChangeStable,
                onPhoneChange = onPhoneChangeStable,
                onAddAddress = onAddAddress,
                onRemoveAddress = onRemoveAddress,
                onAddressChange = onAddressChange,
                onAddCompany = onAddCompany,
                onSave = onSave
            )
        }

        is CreateClientUiState.Error -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) { Text(text = stringResource(state.message))
            }
        }
    }

    if (showImageDialog) {
        CustomOptionsDialog(
            title = stringResource(R.string.message_select_an_option),
            options = listOf(
                DialogOption(
                    text = stringResource(R.string.camera),
                    onClick = {
                        onShowImageDialogChange(false)
                        onLaunchCamera()
                    }
                ),
                DialogOption(
                    text = stringResource(R.string.gallery),
                    onClick = {
                        onShowImageDialogChange(false)
                        onLaunchGallery()
                    }
                )
            ),
            onDismiss = {
                onShowImageDialogChange(false)
            }
        )
    }
}