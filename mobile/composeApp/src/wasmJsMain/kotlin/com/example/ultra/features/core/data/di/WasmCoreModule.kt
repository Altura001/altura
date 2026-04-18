package com.example.ultra.features.core.data.di

import io.ktor.client.engine.wasm.Wasm
import org.koin.dsl.module

actual val coreModule = module {
    single { Wasm.create() }
}
