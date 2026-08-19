package com.example.ultra.di

import com.example.ultra.auth.data.repository.AlturaAuthRepository
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LoginUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.auth.domain.usecase.SignupCustomerUseCase
import com.example.ultra.auth.domain.usecase.SignupVendorUseCase
import com.example.ultra.auth.domain.usecase.SocialAuthUseCase
import com.example.ultra.auth.presentation.viewmodel.AuthViewModel
import com.example.ultra.shopping.cart.data.repository.DefaultCartRepository
import com.example.ultra.shopping.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.GetCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.shopping.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.shopping.cart.presentation.viewmodel.CartViewModel
import com.example.ultra.shopping.home.data.repository.AlturaCatalogRepository
import com.example.ultra.shopping.home.domain.usecase.GetProductsUseCase
import com.example.ultra.shopping.home.domain.usecase.GetVendorsUseCase
import com.example.ultra.shopping.home.domain.usecase.GetPickupStationsUseCase
import com.example.ultra.shopping.home.domain.usecase.SearchProductsUseCase
import com.example.ultra.shopping.home.presentation.viewmodel.HomeViewModel
import com.example.ultra.shopping.home.presentation.productdetail.ProductDetailViewModel
import com.example.ultra.shopping.category.viewmodel.CategoryViewModel
import com.example.ultra.shopping.checkout.data.repository.AlturaOrderRepository
import com.example.ultra.shopping.checkout.data.repository.AlturaPaymentRepository
import com.example.ultra.shopping.checkout.domain.usecase.CancelOrderUseCase
import com.example.ultra.shopping.checkout.domain.usecase.CheckoutUseCase
import com.example.ultra.shopping.checkout.domain.usecase.GetOrdersUseCase
import com.example.ultra.shopping.checkout.domain.usecase.InitiatePaymentUseCase
import com.example.ultra.shopping.checkout.domain.usecase.VerifyPaymentUseCase
import com.example.ultra.shopping.checkout.presentation.viewmodel.CheckoutViewModel
import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.repository.CatalogRepository
import com.example.ultra.core.domain.repository.OrderRepository
import com.example.ultra.core.domain.repository.PaymentRepository
import com.example.ultra.core.domain.repository.WishlistRepository
import com.example.ultra.core.domain.util.getPlatformBackendUrl
import com.example.ultra.core.presentation.notification.NotificationManager
import com.example.ultra.profile.presentation.viewmodel.ProfileViewModel
import com.example.ultra.service_shell.presentation.viewmodel.ServiceShellViewModel
import com.example.ultra.food.presentation.viewmodel.FoodViewModel
import com.example.ultra.rent_a_car.presentation.viewmodel.RentACarViewModel
import com.example.ultra.ticketing.presentation.viewmodel.TicketingViewModel
import com.example.ultra.hotel.presentation.viewmodel.HotelViewModel
import com.example.ultra.health.presentation.viewmodel.HealthViewModel
import com.example.ultra.local_market.presentation.viewmodel.LocalMarketViewModel
import com.example.ultra.shopping.wishlist.data.repository.DefaultWishlistRepository
import com.example.ultra.shopping.wishlist.viewmodel.WishlistViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val coreModule = module {
    single { AlturaApiService(get(), getPlatformBackendUrl(), get()) }
    single { NotificationManager() }
}

val authDataModule = module {
    singleOf(::AlturaAuthRepository) bind AuthRepository::class
}

val authDomainModule = module {
    singleOf(::LoginUseCase)
    singleOf(::SignupCustomerUseCase)
    singleOf(::SignupVendorUseCase)
    singleOf(::LogoutUseCase)
    singleOf(::GetCurrentUserUseCase)
    singleOf(::SocialAuthUseCase)
}

val authPresentationModule = module {
    viewModelOf(::AuthViewModel)
}

val catalogDataModule = module {
    singleOf(::AlturaCatalogRepository) bind CatalogRepository::class
}

val categoryPresentationModule = module {
    singleOf(::CategoryViewModel)
}

val wishlistDataModule = module {
    singleOf(::DefaultWishlistRepository) bind WishlistRepository::class
}

val wishlistPresentationModule = module {
    viewModelOf(::WishlistViewModel)
}

val catalogDomainModule = module {
    singleOf(::GetVendorsUseCase)
    singleOf(::GetProductsUseCase)
    singleOf(::SearchProductsUseCase)
    singleOf(::GetPickupStationsUseCase)
}

val catalogPresentationModule = module {
    viewModelOf(::HomeViewModel)
}

val cartDataModule = module {
    singleOf(::DefaultCartRepository) bind CartRepository::class
}

val cartDomainModule = module {
    singleOf(::GetCartUseCase)
    singleOf(::AddToCartUseCase)
    singleOf(::UpdateCartItemUseCase)
    singleOf(::RemoveFromCartUseCase)
    singleOf(::ClearCartUseCase)
}

val cartPresentationModule = module {
    viewModelOf(::CartViewModel)
}

val checkoutDataModule = module {
    singleOf(::AlturaOrderRepository) bind OrderRepository::class
    singleOf(::AlturaPaymentRepository) bind PaymentRepository::class
}

val checkoutDomainModule = module {
    singleOf(::CheckoutUseCase)
    singleOf(::GetOrdersUseCase)
    singleOf(::CancelOrderUseCase)
    singleOf(::InitiatePaymentUseCase)
    singleOf(::VerifyPaymentUseCase)
}

val checkoutPresentationModule = module {
    viewModelOf(::CheckoutViewModel)
}

val profilePresentationModule = module {
    viewModelOf(::ProfileViewModel)
}

val productDetailPresentationModule = module {
    viewModelOf(::ProductDetailViewModel)
}

// ── Service Shell ──
val serviceShellModule = module {
    viewModelOf(::ServiceShellViewModel)
}

// ── Food Service ──
val foodPresentationModule = module {
    viewModelOf(::FoodViewModel)
}

// ── Rent a Car Service ──
val rentACarPresentationModule = module {
    viewModelOf(::RentACarViewModel)
}

// ── Ticketing Service ──
val ticketingPresentationModule = module {
    viewModelOf(::TicketingViewModel)
}

// ── Hotel / Shortlet Service ──
val hotelPresentationModule = module {
    viewModelOf(::HotelViewModel)
}

// ── Health Service ──
val healthPresentationModule = module {
    viewModelOf(::HealthViewModel)
}

// ── Local Market Service ──
val localMarketPresentationModule = module {
    viewModelOf(::LocalMarketViewModel)
}

val sharedModule: Module = module {
    includes(
        settingsModule,
        coreModule,
        authDataModule,
        authDomainModule,
        authPresentationModule,
        catalogDataModule,
        catalogDomainModule,
        catalogPresentationModule,
        categoryPresentationModule,
        wishlistDataModule,
        wishlistPresentationModule,
        cartDataModule,
        cartDomainModule,
        cartPresentationModule,
        checkoutDataModule,
        checkoutDomainModule,
        checkoutPresentationModule,
        profilePresentationModule,
        productDetailPresentationModule,
        serviceShellModule,
        foodPresentationModule,
        rentACarPresentationModule,
        ticketingPresentationModule,
        hotelPresentationModule,
        healthPresentationModule,
        localMarketPresentationModule
    )
}
