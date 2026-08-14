package com.example.ultra.checkout.presentation.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.checkout.presentation.intent.CheckoutAction
import com.example.ultra.checkout.presentation.intent.CheckoutEvent
import com.example.ultra.checkout.presentation.intent.CheckoutState
import com.example.ultra.checkout.presentation.intent.CheckoutStep
import com.example.ultra.checkout.presentation.viewmodel.CheckoutViewModel
import com.example.ultra.core.presentation.ObserveAsEvents
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutScreenRoot(
    onNavigateBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val uriHandler = LocalUriHandler.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CheckoutEvent.OpenUrl -> uriHandler.openUri(event.url)
            is CheckoutEvent.ShowError -> scope.launch {
                snackbarHostState.showSnackbar(event.message.asString())
            }
            is CheckoutEvent.Done -> onDone()
        }
    }

    CheckoutScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onDone = onDone,
        snackbarHostState = snackbarHostState
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onAction: (CheckoutAction) -> Unit,
    onNavigateBack: () -> Unit,
    onDone: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when (state.step) {
                CheckoutStep.Address -> AddressStep(state, onAction)
                CheckoutStep.Payment -> PaymentStep(state, onAction)
                CheckoutStep.AwaitingConfirmation -> AwaitingStep(state, onAction)
                CheckoutStep.Success -> SuccessStep(onDone)
            }

            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
private fun AddressStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Shipping address", fontWeight = FontWeight.Bold, fontSize = 18.sp)

        OutlinedTextField(state.firstName, { onAction(CheckoutAction.OnFirstNameChange(it)) },
            label = { Text("First name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.lastName, { onAction(CheckoutAction.OnLastNameChange(it)) },
            label = { Text("Last name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.line1, { onAction(CheckoutAction.OnLine1Change(it)) },
            label = { Text("Address line") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.city, { onAction(CheckoutAction.OnCityChange(it)) },
            label = { Text("City") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.postalCode, { onAction(CheckoutAction.OnPostalCodeChange(it)) },
            label = { Text("Postal code") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.country, { onAction(CheckoutAction.OnCountryChange(it)) },
            label = { Text("Country (2-letter code)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(state.phone, { onAction(CheckoutAction.OnPhoneChange(it)) },
            label = { Text("Phone (optional)") }, singleLine = true, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onAction(CheckoutAction.PlaceOrder) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Place order — ${format(state.cartTotal)} ${state.currency}")
        }
    }
}

@Composable
private fun PaymentStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    val order = state.order
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Order placed", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text("Order #${order?.id?.take(8) ?: ""}")
        Text(
            "Total: ${format(order?.total ?: state.cartTotal)} ${order?.currency ?: state.currency}",
            fontWeight = FontWeight.SemiBold
        )
        Text(
            "You'll be taken to Paystack's secure checkout to complete payment.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onAction(CheckoutAction.StartPayment) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Pay with Paystack")
        }
        OutlinedButton(
            onClick = { onAction(CheckoutAction.BackToAddress) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Edit address")
        }
    }
}

@Composable
private fun AwaitingStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Complete your payment", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Text(
            "Finish paying in the Paystack window that just opened. Once done, return here and tap the button below.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        state.paymentInitiation?.let {
            Text("Reference: ${it.reference}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(8.dp))
        Button(
            onClick = { onAction(CheckoutAction.ConfirmPayment) },
            enabled = !state.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("I've completed payment")
        }
        state.paymentInitiation?.authorizationUrl?.let { url ->
            val uriHandler = LocalUriHandler.current
            OutlinedButton(onClick = { uriHandler.openUri(url) }, modifier = Modifier.fillMaxWidth()) {
                Text("Reopen payment page")
            }
        }
    }
}

@Composable
private fun SuccessStep(onDone: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Filled.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Payment successful", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(
            "Thank you! Your order has been paid.",
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(24.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Continue shopping")
        }
    }
}

private fun format(value: Double): String {
    val cents = (value * 100).toLong()
    val whole = cents / 100
    val frac = (cents % 100).toInt().let { if (it < 0) -it else it }
    val fracStr = if (frac < 10) "0$frac" else "$frac"
    return "$whole.$fracStr"
}
