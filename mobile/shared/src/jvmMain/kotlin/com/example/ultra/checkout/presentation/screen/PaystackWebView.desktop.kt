package com.example.ultra.checkout.presentation.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.awt.Desktop
import java.net.URI

/**
 * Desktop (JVM) actual: opens the Paystack checkout URL in the system browser.
 *
 * JavaFX WebView is not compatible with KMP's JVM target due to JPMS modular JAR resolution.
 * The system browser provides a reliable, fully-featured payment experience.
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
    var opened by remember { mutableStateOf(false) }

    LaunchedEffect(authorizationUrl) {
        if (!opened) {
            opened = true
            try {
                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().browse(URI(authorizationUrl))
                } else {
                    onError("Desktop browsing is not supported on this system")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Failed to open payment page")
            }
        }
    }

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Payment page opened in your browser.", color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Complete the payment there, then return here.", color = Color.Gray, fontSize = 13.sp)
        }
    }
}
