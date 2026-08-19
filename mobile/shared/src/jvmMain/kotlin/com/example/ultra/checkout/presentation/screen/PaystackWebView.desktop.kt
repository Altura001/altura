package com.example.ultra.checkout.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ultra.core.presentation.theme.AlturaOrange
import java.awt.Desktop
import java.net.URI

/**
 * Desktop (JVM) actual: opens the Paystack checkout URL in the system browser.
 *
 * JavaFX WebView is not compatible with KMP's JVM target (JPMS modular JARs).
 * We open the system browser and show a manual confirm button for the user
 * to click after completing payment.
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
                }
            } catch (_: Exception) {
                // Browser failed to open — user can still click confirm below
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("A browser window has opened with Paystack.", color = Color.DarkGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Complete the payment there, then click the button below.", color = Color.Gray, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onSuccess(reference) },
            colors = ButtonDefaults.buttonColors(containerColor = AlturaOrange)
        ) {
            Text("I've completed payment", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onCanceled) {
            Text("Cancel", color = Color.Gray)
        }
    }
}
