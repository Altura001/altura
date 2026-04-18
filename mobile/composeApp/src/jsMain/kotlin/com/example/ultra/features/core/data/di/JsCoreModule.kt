package com.example.ultra.features.core.data.di

import io.ktor.client.engine.js.Js
import org.koin.dsl.module

actual val coreModule = module {
    single { Js.create() }
}
