package com.example.ultra.checkout.presentation.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform-specific WebView that loads the Paystack hosted checkout.
 *
 * @param authorizationUrl  The URL returned by POST /api/orders/{id}/pay
 * @param reference         The payment reference to pass to the verify endpoint
 * @param onSuccess         Called when the customer completes payment (url redirected to callback)
 * @param onCanceled        Called when the customer taps Cancel on the checkout page
 * @param onError           Called on any other error / navigation failure
 */
@Composable
expect fun PaystackWebView(
    authorizationUrl: String,
    reference: String,
    onSuccess: (reference: String) -> Unit,
    onCanceled: () -> Unit,
    onError: (message: String) -> Unit,
    modifier: Modifier = Modifier
)
