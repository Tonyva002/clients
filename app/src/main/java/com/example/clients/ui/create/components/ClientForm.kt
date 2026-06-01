package com.example.clients.ui.create.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.clients.R
import com.example.clients.ui.create.states.CreateClientFormState
import com.example.clients.ui.create.states.ValidationErrors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClientForm(
    padding: PaddingValues,
    form: CreateClientFormState,
    errors: ValidationErrors,

    onSelectImage: () -> Unit,

    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onCompanyChange: (Long) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,

    onAddAddress: () -> Unit,
    onRemoveAddress: (Int) -> Unit,
    onAddressChange: (Int, String) -> Unit,

    onAddCompany: () -> Unit,
    onSave: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .imePadding()
            .padding(padding)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = rememberAsyncImagePainter(
                    form.client.photoUri.ifBlank { R.drawable.photo_01 }
                ),
                contentDescription = stringResource(R.string.client_image),

                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable {
                        onSelectImage()
                    },
                contentScale = ContentScale.Crop
            )
        }

        item {

            errors.image?.let {
                Text(text = stringResource(it))
            }
        }


        item {
            // Name
            OutlinedTextField(
                value = form.client.name,

                onValueChange = onNameChange,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text(stringResource(R.string.name))
                },

                isError = errors.name != null,

                supportingText = {
                    errors.name?.let {
                        Text(stringResource(it))
                    }
                }
            )
        }

        item {
            // Lastname
            OutlinedTextField(
                value = form.client.lastname,

                onValueChange = onLastNameChange,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text(stringResource(R.string.lastname))
                },

                isError = errors.lastname != null,

                supportingText = {
                    errors.lastname?.let {
                        Text(stringResource(it))
                    }
                }
            )

        }

        item {
            // Company Selection
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                val selectedCompany = form.companies.find { it.id == form.client.companyId }
                
                OutlinedTextField(
                    value = selectedCompany?.name ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.company)) },
                    leadingIcon = selectedCompany?.let {
                        {
                            Image(
                                painter = rememberAsyncImagePainter(
                                    it.logoUri.ifBlank { R.drawable.photo_01 }
                                ),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                    },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth(),
                    isError = errors.company != null,
                    supportingText = {
                        errors.company?.let { Text(stringResource(it)) }
                    }
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null)
                                Spacer(Modifier.width(8.dp))
                                Text("Añadir nueva compañía")
                            }
                        },
                        onClick = {
                            onAddCompany()
                            expanded = false
                        }
                    )

                    form.companies.forEach { company ->
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Image(
                                        painter = rememberAsyncImagePainter(
                                            company.logoUri.ifBlank { R.drawable.photo_01 }
                                        ),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape),
                                        contentScale = ContentScale.Crop
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(company.name)
                                }
                            },
                            onClick = {
                                onCompanyChange(company.id)
                                expanded = false
                            }
                        )
                    }
                }
            }
        }

        item {
            // Email
            OutlinedTextField(
                value = form.client.email,

                onValueChange = onEmailChange,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text(stringResource(R.string.email))
                },

                isError = errors.email != null,

                supportingText = {
                    errors.email?.let {
                        Text(stringResource(it))
                    }
                }
            )
        }

        item {

            // Phone
            OutlinedTextField(
                value = form.client.phone,

                onValueChange = onPhoneChange,

                modifier = Modifier.fillMaxWidth(),

                label = {
                    Text(stringResource(R.string.phone))
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone
                ),

                isError = errors.phone != null,

                supportingText = {
                    errors.phone?.let {
                        Text(stringResource(it))
                    }
                }
            )

        }

        item {
            Text(
                text = stringResource(R.string.addresses),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            )
        }

        itemsIndexed(form.addresses) { index, address ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = address.fullAddress,
                    onValueChange = { newValue -> onAddressChange(index, newValue) },
                    modifier = Modifier.weight(1f),
                    label = {
                        Text("${stringResource(R.string.address)} ${index + 1}")
                    },
                    isError = index == 0 && errors.address1 != null,
                    supportingText = {
                        if (index == 0) {
                            errors.address1?.let {
                                Text(stringResource(it))
                            }
                        }
                    }
                )

                if (form.addresses.size > 1) {
                    IconButton(onClick = { onRemoveAddress(index) }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(R.string.delete)
                        )
                    }
                }
            }
        }

        item {
            TextButton(
                onClick = onAddAddress,
                modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(Modifier.width(4.dp))
                Text(stringResource(R.string.add_address))
            }
        }

        item {

            Spacer(Modifier.height(8.dp))

        }

        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.save))
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
