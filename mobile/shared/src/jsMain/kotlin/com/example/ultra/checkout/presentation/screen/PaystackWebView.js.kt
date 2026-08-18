package com.example.ultra.checkout.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

/**
 * WebView is not supported on JS targets.
 * Payments are not expected to run in a browser context.
 */
@Composable
actual fun PaystackWebView(
    authorizationUrl: String,
    reference: String,
    onSuccess: (reference: String) -> Unit,
    onCanceled: () -> Unit,
    onError: (message: String) -> Unit,
    modifier: Modifier
) {
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Payment is not available on this platform.", color = Color.Gray)
    }
}
