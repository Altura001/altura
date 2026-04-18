package com.example.ultra.features.core.data.di

import io.ktor.client.engine.ios.Ios
import org.koin.dsl.module

actual val coreModule = module {
    single { Ios.create() }
}
