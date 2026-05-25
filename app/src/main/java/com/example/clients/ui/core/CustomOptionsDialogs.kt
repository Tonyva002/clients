package com.example.clients.ui.core

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


data class DialogOption(
    val text: String,
    val onClick: () -> Unit
)

@Composable
fun CustomOptionsDialog(
    title: String,
    message: String? = null,
    options: List<DialogOption>,
    onDismiss: () -> Unit,
    dismissText: String = "Cancelar"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(title)
        },
        text = {
            Column {
                message?.let {
                    Text(it)
                }

                options.forEach { option ->
                    Button(
                        onClick = option.onClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp)
                    ) {
                        Text(option.text)
                    }
                }
            }
        },
        confirmButton = {},

        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(dismissText)
            }
        }
    )
}
