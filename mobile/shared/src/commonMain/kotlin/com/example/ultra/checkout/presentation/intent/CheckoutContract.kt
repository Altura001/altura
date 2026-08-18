package com.example.ultra.checkout.presentation.intent

import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.model.PickupStation
import com.example.ultra.core.presentation.UiText

enum class CheckoutStep { Delivery, Payment, Summary, Success }

enum class DeliveryMethod { Pickup, Shipping }

data class CheckoutState(
    val step: CheckoutStep = CheckoutStep.Delivery,
    val isLoading: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val line1: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val phone: String = "",
    val cartTotal: Double = 0.0,
    val shippingFee: Double = 0.0,
    val pickupFee: Double = 0.0,
    val currency: String = "EUR",
    val deliveryMethod: DeliveryMethod = DeliveryMethod.Pickup,
    val pickupStations: List<PickupStation> = emptyList(),
    val selectedStation: PickupStation? = null,
    val order: Order? = null,
    val paymentInitiation: PaymentInitiation? = null,
    val error: UiText? = null
)

sealed interface CheckoutAction {
    data class OnFirstNameChange(val value: String) : CheckoutAction
    data class OnLastNameChange(val value: String) : CheckoutAction
    data class OnLine1Change(val value: String) : CheckoutAction
    data class OnCityChange(val value: String) : CheckoutAction
    data class OnPostalCodeChange(val value: String) : CheckoutAction
    data class OnCountryChange(val value: String) : CheckoutAction
    data class OnPhoneChange(val value: String) : CheckoutAction
    
    data class SelectTab(val step: CheckoutStep) : CheckoutAction
    data class SelectDeliveryMethod(val method: DeliveryMethod) : CheckoutAction
    data class SelectPickupStation(val station: PickupStation) : CheckoutAction

    data object PlaceOrder : CheckoutAction
    data object StartPayment : CheckoutAction
    data object ConfirmPayment : CheckoutAction
    data object BackToAddress : CheckoutAction
}

sealed interface CheckoutEvent {
    data class OpenUrl(val url: String) : CheckoutEvent
    data class ShowError(val message: UiText) : CheckoutEvent
    data object Done : CheckoutEvent
}
