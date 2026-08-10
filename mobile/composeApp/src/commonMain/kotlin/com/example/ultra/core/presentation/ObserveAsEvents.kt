package com.example.ultra.core.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import kotlinx.coroutines.flow.Flow

@Composable
fun <T> ObserveAsEvents(
    events: Flow<T>,
    onEvent: (T) -> Unit
) {
    LaunchedEffect(Unit) {
        events.collect { onEvent(it) }
    }
}
