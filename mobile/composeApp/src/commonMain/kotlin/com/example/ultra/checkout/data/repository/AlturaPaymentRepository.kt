package com.example.ultra.checkout.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.toModel
import com.example.ultra.core.data.toOrder
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.repository.PaymentRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result

/** PaymentRepository backed by the Altura Nova .NET backend (Paystack). */
class AlturaPaymentRepository(
    private val api: AlturaApiService
) : PaymentRepository {

    override suspend fun initiatePayment(
        orderId: String,
        callbackUrl: String?
    ): Result<PaymentInitiation, DataError.Network> =
        safeApiCall { api.initiatePayment(orderId, callbackUrl).toModel() }

    override suspend fun verifyPayment(orderId: String): Result<Order, DataError.Network> =
        safeApiCall { api.verifyPayment(orderId).toOrder() }
}
