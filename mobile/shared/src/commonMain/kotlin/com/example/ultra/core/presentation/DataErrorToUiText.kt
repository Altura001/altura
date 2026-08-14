package com.example.ultra.core.presentation

import com.example.ultra.core.domain.util.DataError

fun DataError.toUiText(): UiText = when (this) {
    DataError.Network.BAD_REQUEST -> UiText.DynamicString("The request was invalid. Please check your input.")
    DataError.Network.REQUEST_TIMEOUT -> UiText.DynamicString("The request timed out. Please try again.")
    DataError.Network.UNAUTHORIZED -> UiText.DynamicString("Invalid email or password.")
    DataError.Network.FORBIDDEN -> UiText.DynamicString("You don't have permission to do that.")
    DataError.Network.NOT_FOUND -> UiText.DynamicString("The requested resource was not found.")
    DataError.Network.CONFLICT -> UiText.DynamicString("Something changed on the server. Please refresh and try again.")
    DataError.Network.TOO_MANY_REQUESTS -> UiText.DynamicString("Too many requests. Please wait a moment.")
    DataError.Network.NO_INTERNET -> UiText.DynamicString("No internet connection.")
    DataError.Network.SERVER -> UiText.DynamicString("Server error. Please try again later.")
    DataError.Network.SERIALIZATION -> UiText.DynamicString("Failed to process server response.")
    DataError.Network.UNKNOWN -> UiText.DynamicString("An unexpected error occurred.")
    DataError.Local.DISK_FULL -> UiText.DynamicString("Device storage is full.")
    DataError.Local.UNKNOWN -> UiText.DynamicString("A local storage error occurred.")
}
