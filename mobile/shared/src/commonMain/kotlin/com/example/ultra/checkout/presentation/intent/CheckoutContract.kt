package com.example.ultra.checkout.presentation.intent

import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.presentation.UiText

enum class CheckoutStep { Address, Payment, AwaitingConfirmation, Success }

data class CheckoutState(
    val step: CheckoutStep = CheckoutStep.Address,
    val isLoading: Boolean = false,
    val firstName: String = "",
    val lastName: String = "",
    val line1: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "NG",
    val phone: String = "",
    val cartTotal: Double = 0.0,
    val currency: String = "NGN",
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
