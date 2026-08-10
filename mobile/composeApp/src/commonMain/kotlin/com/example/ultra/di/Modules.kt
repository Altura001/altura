package com.example.ultra.di

import com.example.ultra.auth.data.repository.AlturaAuthRepository
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LoginUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.auth.domain.usecase.SignupCustomerUseCase
import com.example.ultra.auth.domain.usecase.SignupVendorUseCase
import com.example.ultra.auth.domain.usecase.SocialAuthUseCase
import com.example.ultra.auth.presentation.viewmodel.AuthViewModel
import com.example.ultra.cart.data.repository.AlturaCartRepository
import com.example.ultra.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.cart.domain.usecase.ClearCartUseCase
import com.example.ultra.cart.domain.usecase.GetCartUseCase
import com.example.ultra.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.cart.presentation.viewmodel.CartViewModel
import com.example.ultra.catalog.data.repository.AlturaCatalogRepository
import com.example.ultra.catalog.domain.usecase.GetProductsUseCase
import com.example.ultra.catalog.domain.usecase.GetVendorsUseCase
import com.example.ultra.catalog.presentation.viewmodel.CatalogViewModel
import com.example.ultra.catalog.presentation.productdetail.ProductDetailViewModel
import com.example.ultra.checkout.data.repository.AlturaOrderRepository
import com.example.ultra.checkout.data.repository.AlturaPaymentRepository
import com.example.ultra.checkout.domain.usecase.CancelOrderUseCase
import com.example.ultra.checkout.domain.usecase.CheckoutUseCase
import com.example.ultra.checkout.domain.usecase.GetOrdersUseCase
import com.example.ultra.checkout.domain.usecase.InitiatePaymentUseCase
import com.example.ultra.checkout.domain.usecase.VerifyPaymentUseCase
import com.example.ultra.checkout.presentation.viewmodel.CheckoutViewModel
import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.repository.CatalogRepository
import com.example.ultra.core.domain.repository.OrderRepository
import com.example.ultra.core.domain.repository.PaymentRepository
import com.example.ultra.core.domain.util.getPlatformBackendUrl
import com.example.ultra.profile.presentation.viewmodel.ProfileViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val coreModule = module {
    single { AlturaApiService(get(), getPlatformBackendUrl(), get()) }
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

val catalogDomainModule = module {
    singleOf(::GetVendorsUseCase)
    singleOf(::GetProductsUseCase)
}

val catalogPresentationModule = module {
    viewModelOf(::CatalogViewModel)
}

val cartDataModule = module {
    singleOf(::AlturaCartRepository) bind CartRepository::class
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
        cartDataModule,
        cartDomainModule,
        cartPresentationModule,
        checkoutDataModule,
        checkoutDomainModule,
        checkoutPresentationModule,
        profilePresentationModule,
        productDetailPresentationModule
    )
}
