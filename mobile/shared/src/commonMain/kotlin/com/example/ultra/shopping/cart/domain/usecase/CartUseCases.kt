package com.example.ultra.shopping.cart.domain.usecase

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result

class GetCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(): Result<Cart, DataError> {
        return repository.getCart()
    }
}

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Result<Cart, DataError> {
        return repository.addToCart(product, quantity)
    }
}

class UpdateCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, quantity: Int): Result<Cart, DataError> {
        return repository.updateCartItem(cartItemId, quantity)
    }
}

class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: String): Result<Cart, DataError> {
        return repository.removeFromCart(cartItemId)
    }
}

class ClearCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(): Result<Cart, DataError> {
        return repository.clearCart()
    }
}
