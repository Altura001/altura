package com.example.ultra.cart.domain.usecase

import com.example.ultra.core.domain.model.Cart
import com.example.ultra.core.domain.model.Product
import com.example.ultra.core.domain.repository.CartRepository

class GetCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(): Cart {
        return repository.getCart()
    }
}

class AddToCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(product: Product, quantity: Int = 1): Cart {
        return repository.addToCart(product, quantity)
    }
}

class UpdateCartItemUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: String, quantity: Int): Cart {
        return repository.updateCartItem(cartItemId, quantity)
    }
}

class RemoveFromCartUseCase(private val repository: CartRepository) {
    suspend operator fun invoke(cartItemId: String): Cart {
        return repository.removeFromCart(cartItemId)
    }
}
