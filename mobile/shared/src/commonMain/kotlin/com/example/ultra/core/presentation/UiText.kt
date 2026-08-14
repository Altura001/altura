package com.example.ultra.core.presentation

import androidx.compose.runtime.Composable

sealed interface UiText {
    data class DynamicString(val value: String) : UiText

    fun asString(): String {
        return when (this) {
            is DynamicString -> value
        }
    }

    @Suppress("NOTHING_TO_INLINE")
    @Composable
    fun asComposeString(): String = asString()
}
