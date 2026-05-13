package com.example.clients.ui.create.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun CreateClientContent(
    form: CreateClientFormState,
    errors: ValidationErrors,

    onSelectImage: () -> Unit,

    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onCompanyChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,

    onAddress1Change: (String) -> Unit,
    onAddress2Change: (String) -> Unit,

    onSave: () -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .statusBarsPadding()
            .fillMaxSize()
            .imePadding()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Image(
                painter = rememberAsyncImagePainter(
                    form.client.photoUri.ifBlank { R.drawable.photo_01 }
                ),
                contentDescription = null,

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

                modifier = Modifier.fillParentMaxWidth(),

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

                modifier = Modifier.fillParentMaxWidth(),

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
            // Company
            OutlinedTextField(
                value = form.client.company,

                onValueChange = onCompanyChange,

                modifier = Modifier.fillParentMaxWidth(),

                label = {
                    Text(stringResource(R.string.company))
                },

                isError = errors.company != null,

                supportingText = {
                    errors.company?.let {
                        Text(stringResource(it))
                    }
                }
            )
        }

        item {
            // Email
            OutlinedTextField(
                value = form.client.email,

                onValueChange = onEmailChange,

                modifier = Modifier.fillParentMaxWidth(),

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

                modifier = Modifier.fillParentMaxWidth(),

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

            // Address 1
            OutlinedTextField(

                value = form.addresses
                    .getOrNull(0)
                    ?.fullAddress
                    .orEmpty(),

                onValueChange = onAddress1Change,

                modifier = Modifier.fillParentMaxWidth(),

                label = {
                    Text(stringResource(R.string.address1))
                },

                isError = errors.address1 != null,

                supportingText = {
                    errors.address1?.let {
                        Text(stringResource(it))
                    }
                }
            )

        }

        item {

            // Address 2
            OutlinedTextField(

                value = form.addresses
                    .getOrNull(1)
                    ?.fullAddress
                    .orEmpty(),

                onValueChange = onAddress2Change,

                modifier = Modifier.fillParentMaxWidth(),

                label = {
                    Text(stringResource(R.string.address2))
                }
            )

        }

        item {

            Spacer(Modifier.height(8.dp))

        }

        item {
            Button(
                onClick = onSave,
                modifier = Modifier.fillParentMaxWidth(),
            ) {
                Text(stringResource(R.string.save))
            }
        }
        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}