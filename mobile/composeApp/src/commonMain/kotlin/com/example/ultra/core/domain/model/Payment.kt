package com.example.ultra.core.domain.model

/** Details returned when a hosted-checkout payment is initialized. */
data class PaymentInitiation(
    val orderId: String,
    val provider: String,
    val authorizationUrl: String,
    val reference: String,
    val publicKey: String,
    val amountSubunits: Long,
    val currency: String
)
