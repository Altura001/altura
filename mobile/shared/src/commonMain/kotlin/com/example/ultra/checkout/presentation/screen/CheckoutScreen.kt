package com.example.ultra.checkout.presentation.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ultra.checkout.presentation.intent.*
import com.example.ultra.checkout.presentation.viewmodel.CheckoutViewModel
import com.example.ultra.core.data.util.formatCurrency
import com.example.ultra.core.presentation.ObserveAsEvents
import com.example.ultra.core.presentation.theme.AlturaBlue
import com.example.ultra.core.presentation.theme.AlturaOrange
import com.example.ultra.core.presentation.theme.AlturaYellow
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CheckoutScreenRoot(
    onNavigateBack: () -> Unit,
    onDone: () -> Unit,
    viewModel: CheckoutViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val uriHandler = LocalUriHandler.current

    ObserveAsEvents(viewModel.events) { event ->
        when (event) {
            is CheckoutEvent.OpenUrl -> uriHandler.openUri(event.url)
            is CheckoutEvent.ShowError -> {}
            is CheckoutEvent.Done -> onDone()
        }
    }

    CheckoutScreen(
        state = state,
        onAction = viewModel::onAction,
        onNavigateBack = onNavigateBack,
        onDone = onDone
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    state: CheckoutState,
    onAction: (CheckoutAction) -> Unit,
    onNavigateBack: () -> Unit,
    onDone: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color.White
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            CheckoutTabs(
                currentStep = state.step,
                onTabSelected = { onAction(CheckoutAction.SelectTab(it)) }
            )

            Box(modifier = Modifier.weight(1f)) {
                when (state.step) {
                    CheckoutStep.Delivery -> DeliveryStep(state, onAction)
                    CheckoutStep.Payment -> PaymentStep(state, onAction)
                    CheckoutStep.Summary -> SummaryStep(state, onAction)
                    CheckoutStep.Success -> SuccessStep(onDone)
                }

                if (state.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
            }

            if (state.step != CheckoutStep.Success) {
                CheckoutFooter(state)
            }
        }
    }
}

@Composable
private fun CheckoutTabs(
    currentStep: CheckoutStep,
    onTabSelected: (CheckoutStep) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CheckoutTabItem(
            title = "Delivery",
            isActive = currentStep == CheckoutStep.Delivery,
            onClick = { onTabSelected(CheckoutStep.Delivery) }
        )
        CheckoutTabItem(
            title = "Payment",
            isActive = currentStep == CheckoutStep.Payment,
            onClick = { onTabSelected(CheckoutStep.Payment) }
        )
        CheckoutTabItem(
            title = "Summary",
            isActive = currentStep == CheckoutStep.Summary,
            onClick = { onTabSelected(CheckoutStep.Summary) }
        )
    }
    HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))
}

@Composable
private fun CheckoutTabItem(
    title: String,
    isActive: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isActive) AlturaOrange else Color.Transparent)
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (isActive) Color.White else Color.Gray,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun DeliveryStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    var showAddressDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Address Detail Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ADDRESS DETAIL",
                color = AlturaBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Text(
                text = "Change",
                color = AlturaYellow,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                modifier = Modifier.clickable { showAddressDialog = true }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        // Address Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = "${state.firstName} ${state.lastName}".trim(), fontWeight = FontWeight.Bold, color = Color.Gray)
            if (state.line1.isNotBlank()) Text(text = state.line1, color = Color.Gray)
            if (state.city.isNotBlank()) Text(text = state.city, color = Color.Gray)
            if (state.phone.isNotBlank()) Text(text = state.phone, color = Color.Gray)
        }
        Spacer(modifier = Modifier.height(8.dp).fillMaxWidth().background(Color(0xFFF9F9F9)))

        // Delivery Method Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = "SELECT DELIVERY METHOD",
                color = AlturaBlue,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        // Pickup Station Option
        DeliveryMethodItem(
            title = "Pickup Station",
            subtitle = "Pick up from a station near you",
            isSelected = state.deliveryMethod == DeliveryMethod.Pickup,
            onClick = { onAction(CheckoutAction.SelectDeliveryMethod(DeliveryMethod.Pickup)) }
        )

        if (state.deliveryMethod == DeliveryMethod.Pickup) {
            PickupStationDropdown(
                stations = state.pickupStations,
                selectedStation = state.selectedStation,
                onStationSelected = { onAction(CheckoutAction.SelectPickupStation(it)) }
            )
        }
        HorizontalDivider(thickness = 1.dp, color = Color(0xFFEEEEEE))

        // Standard Shipping Option
        DeliveryMethodItem(
            title = "Standard Shipping",
            subtitle = "Door delivery to your address",
            isSelected = state.deliveryMethod == DeliveryMethod.Shipping,
            onClick = { onAction(CheckoutAction.SelectDeliveryMethod(DeliveryMethod.Shipping)) }
        )
        Spacer(modifier = Modifier.height(16.dp))
    }

    if (showAddressDialog) {
        AddressDialog(
            state = state,
            onAction = onAction,
            onDismiss = { showAddressDialog = false }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PickupStationDropdown(
    stations: List<com.example.ultra.core.domain.model.PickupStation>,
    selectedStation: com.example.ultra.core.domain.model.PickupStation?,
    onStationSelected: (com.example.ultra.core.domain.model.PickupStation) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
    ) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                value = selectedStation?.let { "${it.name} - ${it.city}" } ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Select a pickup station") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )

            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                stations.forEach { station ->
                    DropdownMenuItem(
                        text = {
                            Column {
                                Text(text = station.name, fontWeight = FontWeight.Bold)
                                Text(text = "${station.address}, ${station.city}", fontSize = 12.sp, color = Color.Gray)
                                station.operatingHours?.let {
                                    Text(text = it, fontSize = 11.sp, color = Color.Gray)
                                }
                            }
                        },
                        onClick = {
                            onStationSelected(station)
                            expanded = false
                        }
                    )
                }
                if (stations.isEmpty()) {
                    DropdownMenuItem(
                        text = { Text("No pickup stations available", color = Color.Gray) },
                        onClick = { expanded = false }
                    )
                }
            }
        }
    }
}

@Composable
private fun AddressDialog(
    state: CheckoutState,
    onAction: (CheckoutAction) -> Unit,
    onDismiss: () -> Unit
) {
    var firstName by remember { mutableStateOf(state.firstName) }
    var lastName by remember { mutableStateOf(state.lastName) }
    var line1 by remember { mutableStateOf(state.line1) }
    var city by remember { mutableStateOf(state.city) }
    var postalCode by remember { mutableStateOf(state.postalCode) }
    var country by remember { mutableStateOf(state.country) }
    var phone by remember { mutableStateOf(state.phone) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Address", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    label = { Text("First Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    label = { Text("Last Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = line1,
                    onValueChange = { line1 = it },
                    label = { Text("Street Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = postalCode,
                    onValueChange = { postalCode = it },
                    label = { Text("Postal Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = country,
                    onValueChange = { country = it.uppercase() },
                    label = { Text("Country (2-letter)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Phone") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onAction(CheckoutAction.OnFirstNameChange(firstName))
                onAction(CheckoutAction.OnLastNameChange(lastName))
                onAction(CheckoutAction.OnLine1Change(line1))
                onAction(CheckoutAction.OnCityChange(city))
                onAction(CheckoutAction.OnPostalCodeChange(postalCode))
                onAction(CheckoutAction.OnCountryChange(country))
                onAction(CheckoutAction.OnPhoneChange(phone))
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun DeliveryMethodItem(
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .border(2.dp, if (isSelected) AlturaYellow else Color.Gray, CircleShape)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isSelected) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(AlturaYellow)
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = title, fontWeight = FontWeight.Bold, color = Color.Gray)
            Text(text = subtitle, fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
private fun CheckoutFooter(state: CheckoutState) {
    val deliveryFee = if (state.deliveryMethod == DeliveryMethod.Shipping) state.shippingFee else state.pickupFee
    val deliveryLabel = if (state.deliveryMethod == DeliveryMethod.Shipping) "Shipping" else "Pickup Fee"
    val orderTotal = state.cartTotal + deliveryFee

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        HorizontalDivider(thickness = 2.dp, color = Color(0xFFEEEEEE))
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Subtotal", fontWeight = FontWeight.Bold)
            Text(text = "N ${state.cartTotal.formatCurrency()}", fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = deliveryLabel, fontWeight = FontWeight.Bold)
            Text(text = "N ${deliveryFee.formatCurrency()}", fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        HorizontalDivider(thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Total", fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Text(
                text = "N ${orderTotal.formatCurrency()}",
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        }
    }
}

@Composable
private fun PaymentStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    val paymentInitiation = state.paymentInitiation

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (paymentInitiation == null) {
            // Payment not yet initiated — show order summary + Pay button
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF9F9F9))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Order Summary",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = AlturaBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    state.order?.let { order ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "Order ID", color = Color.Gray)
                            Text(text = order.id.take(8).uppercase(), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Total Amount", color = Color.Gray)
                        Text(
                            text = "N ${(state.cartTotal + (if (state.deliveryMethod == DeliveryMethod.Shipping) state.shippingFee else state.pickupFee)).formatCurrency()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AlturaOrange
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Payment Method", color = Color.Gray)
                        Text(text = "Paystack", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Tap the button below to pay securely with Paystack.",
                color = Color.Gray,
                fontSize = 14.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { onAction(CheckoutAction.StartPayment) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = AlturaOrange)
            ) {
                Text("Pay with Paystack", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        } else {
            // Payment initiated — show inline Paystack WebView
            PaystackWebView(
                authorizationUrl = paymentInitiation.authorizationUrl,
                reference = paymentInitiation.reference,
                onSuccess = { ref ->
                    onAction(CheckoutAction.ConfirmPayment)
                },
                onCanceled = {
                    onAction(CheckoutAction.BackToAddress)
                },
                onError = { message ->
                    onAction(CheckoutAction.BackToAddress)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryStep(state: CheckoutState, onAction: (CheckoutAction) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Review your order details.", color = Color.Gray)
        Spacer(Modifier.height(16.dp))
        Button(onClick = { onAction(CheckoutAction.PlaceOrder) }, modifier = Modifier.fillMaxWidth()) {
            Text("Place Order")
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
            tint = AlturaOrange,
            modifier = Modifier.size(72.dp)
        )
        Spacer(Modifier.height(16.dp))
        Text("Order Successful!", fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Spacer(Modifier.height(24.dp))
        Button(onClick = onDone, modifier = Modifier.fillMaxWidth()) {
            Text("Continue shopping")
        }
    }
}
