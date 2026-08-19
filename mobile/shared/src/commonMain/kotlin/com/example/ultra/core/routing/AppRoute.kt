package com.example.ultra.core.routing

import kotlinx.serialization.Serializable

// ── Routes ──
sealed interface AppRoute {
    val route: String

    // Launcher
    @Serializable
    object Launcher : AppRoute {
        override val route: String = "launcher"
    }

    // Shared
    @Serializable
    object Auth : AppRoute {
        override val route: String = "auth"
    }

    // Food service
    @Serializable
    object FoodHome : AppRoute {
        override val route: String = "food/home"
    }

    // Shopping service
    @Serializable
    object ShoppingHome : AppRoute {
        override val route: String = "shopping/home"
    }

    @Serializable
    object ShoppingCategory : AppRoute {
        override val route: String = "shopping/category"
    }

    @Serializable
    object ShoppingWishlist : AppRoute {
        override val route: String = "shopping/wishlist"
    }

    @Serializable
    object ShoppingCart : AppRoute {
        override val route: String = "shopping/cart"
    }

    @Serializable
    object ShoppingAccount : AppRoute {
        override val route: String = "shopping/account"
    }

    @Serializable
    object ShoppingCheckout : AppRoute {
        override val route: String = "shopping/checkout"
    }

    @Serializable
    data class ShoppingProductDetail(val handle: String) : AppRoute {
        override val route: String = "shopping/product_detail/$handle"
    }
}