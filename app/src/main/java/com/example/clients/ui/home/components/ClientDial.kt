package com.example.clients.ui.home.components

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.net.toUri
import com.example.clients.R

fun dial(
    context: Context,
    phone: String
) {
    val intent = Intent(
        Intent.ACTION_DIAL,
        "tel:$phone".toUri()
    )

    runCatching {
        context.startActivity(intent)
    }.onFailure {
        Toast.makeText(
            context,
            R.string.message_no_compatible_app_found,
            Toast.LENGTH_SHORT
        ).show()
    }
}