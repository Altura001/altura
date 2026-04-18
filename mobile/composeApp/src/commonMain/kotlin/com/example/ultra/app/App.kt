package com.example.ultra.app

import androidx.compose.runtime.Composable
import com.example.ultra.core.presentation.theme.UltraTheme
import com.example.ultra.di.platformModule
import com.example.ultra.di.sharedModule
import com.example.ultra.navigation.presentation.MainScreen
import org.koin.compose.KoinApplication

@Composable
fun App() {
    UltraTheme {
        KoinApplication(application = {
            modules(
                platformModule,
                sharedModule
            )
        }) {
            MainScreen()
        }
    }
}
