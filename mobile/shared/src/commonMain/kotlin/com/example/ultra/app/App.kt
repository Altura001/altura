package com.example.ultra.app

import androidx.compose.runtime.Composable
import com.example.ultra.core.presentation.theme.UltraTheme
import com.example.ultra.di.platformModule
import com.example.ultra.di.sharedModule
import com.example.ultra.navigation.presentation.MainScreen
import org.koin.compose.KoinApplication
import org.koin.core.KoinApplication
import org.koin.dsl.koinConfiguration

@Composable
fun App() {
    UltraTheme {
        KoinApplication(configuration = koinConfiguration(declaration = {
            modules(
                platformModule,
                sharedModule
            )
        }), content = {
            MainScreen()
        })
    }
}
