package com.example.ultra.di

import com.example.ultra.core.data.TokenStorage
import com.russhwolf.settings.Settings
import org.koin.dsl.module

val settingsModule = module {
    single { Settings() }
    single { TokenStorage(get()) }
}
