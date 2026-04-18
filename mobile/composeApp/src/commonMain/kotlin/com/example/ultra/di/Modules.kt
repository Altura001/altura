package com.example.ultra.di

import com.example.ultra.AppConfig
import com.example.ultra.auth.data.repository.DefaultAuthRepository
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LoginUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.auth.domain.usecase.SocialAuthUseCase
import com.example.ultra.auth.presentation.viewmodel.AuthViewModel
import com.example.ultra.cart.data.repository.DefaultCartRepository
import com.example.ultra.cart.domain.usecase.AddToCartUseCase
import com.example.ultra.cart.domain.usecase.GetCartUseCase
import com.example.ultra.cart.domain.usecase.RemoveFromCartUseCase
import com.example.ultra.cart.domain.usecase.UpdateCartItemUseCase
import com.example.ultra.cart.presentation.viewmodel.CartViewModel
import com.example.ultra.catalog.data.repository.DefaultCatalogRepository
import com.example.ultra.catalog.domain.usecase.GetProductsUseCase
import com.example.ultra.catalog.domain.usecase.GetVendorsUseCase
import com.example.ultra.catalog.presentation.viewmodel.CatalogViewModel
import com.example.ultra.core.data.HttpClientFactory
import com.example.ultra.core.data.MedusaApiService
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.repository.CatalogRepository
import com.example.ultra.profile.presentation.viewmodel.ProfileViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule: Module = module {
    single { MedusaApiService(get(), AppConfig.MEDUSA_BACKEND_URL, AppConfig.MEDUSA_PUBLISHABLE_KEY) }
    
    single<AuthRepository> { DefaultAuthRepository(get()) }
    single<CatalogRepository> { DefaultCatalogRepository(get()) }
    single<CartRepository> { DefaultCartRepository(get()) }
    
    factory { LoginUseCase(get()) }
    factory { LogoutUseCase(get()) }
    factory { GetCurrentUserUseCase(get()) }
    factory { SocialAuthUseCase(get()) }
    
    factory { GetVendorsUseCase(get()) }
    factory { GetProductsUseCase(get()) }
    
    factory { GetCartUseCase(get()) }
    factory { AddToCartUseCase(get()) }
    factory { UpdateCartItemUseCase(get()) }
    factory { RemoveFromCartUseCase(get()) }
    
    factory { AuthViewModel(get(), get(), get(), get()) }
    factory { CatalogViewModel(get(), get()) }
    factory { CartViewModel(get(), get(), get()) }
    factory { ProfileViewModel(get(), get()) }
}
