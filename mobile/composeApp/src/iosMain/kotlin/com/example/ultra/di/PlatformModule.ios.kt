package com.example.ultra.di

import com.example.ultra.core.data.HttpClientFactory
import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.ios.Ios
import org.koin.dsl.module

actual val platformModule: Module = module {
    single<HttpClientEngine> { Ios.create() }
    single { HttpClientFactory(get()).create() }
}
