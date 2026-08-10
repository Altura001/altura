package com.example.ultra.checkout.domain.usecase

import com.example.ultra.core.domain.model.Address
import com.example.ultra.core.domain.model.Order
import com.example.ultra.core.domain.model.PaymentInitiation
import com.example.ultra.core.domain.repository.OrderRepository
import com.example.ultra.core.domain.repository.PaymentRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result

class CheckoutUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(address: Address): Result<Order, DataError.Network> =
        repository.checkout(address)
}

class GetOrdersUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(): Result<List<Order>, DataError.Network> =
        repository.getOrders()
}

class CancelOrderUseCase(private val repository: OrderRepository) {
    suspend operator fun invoke(orderId: String): Result<Order, DataError.Network> =
        repository.cancelOrder(orderId)
}

class InitiatePaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(
        orderId: String,
        callbackUrl: String? = null
    ): Result<PaymentInitiation, DataError.Network> =
        repository.initiatePayment(orderId, callbackUrl)
}

class VerifyPaymentUseCase(private val repository: PaymentRepository) {
    suspend operator fun invoke(orderId: String): Result<Order, DataError.Network> =
        repository.verifyPayment(orderId)
}
