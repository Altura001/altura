package com.example.ultra.core.data

import com.russhwolf.settings.Settings
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class LocalCheckoutStorage(private val settings: Settings) {

    companion object {
        private const val KEY_CHECKOUT_INFO = "local_checkout_info"
    }

    private val json = Json { ignoreUnknownKeys = true }

    fun getCheckoutInfo(): CheckoutInfo {
        val jsonStr = settings.getStringOrNull(KEY_CHECKOUT_INFO) ?: return CheckoutInfo()
        return try {
            json.decodeFromString(jsonStr)
        } catch (_: Exception) {
            CheckoutInfo()
        }
    }

    fun saveCheckoutInfo(info: CheckoutInfo) {
        settings.putString(KEY_CHECKOUT_INFO, json.encodeToString(info))
    }

    /*fun clear() {
        settings.remove(KEY_CHECKOUT_INFO)
    }*/
}

@Serializable
data class CheckoutInfo(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val line1: String = "",
    val city: String = "",
    val postalCode: String = "",
    val country: String = "",
    val phone: String = ""
)
