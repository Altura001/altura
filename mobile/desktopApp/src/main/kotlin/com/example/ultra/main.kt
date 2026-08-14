package com.example.ultra

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import androidx.compose.ui.unit.DpSize
import com.example.ultra.app.App

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Ultra",
        state = rememberWindowState(size = DpSize(420.dp, 800.dp)),
    ) {
        App()
    }
}
