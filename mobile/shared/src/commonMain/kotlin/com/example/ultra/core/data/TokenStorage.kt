package com.example.ultra.core.data

import com.russhwolf.settings.Settings

class TokenStorage(private val settings: Settings) {

    companion object {
        private const val KEY_ACCESS_TOKEN = "access_token"
        private const val KEY_REFRESH_TOKEN = "refresh_token"
        private const val KEY_ACCESS_EXPIRES_AT = "access_expires_at" // epoch seconds
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_FIRST_NAME = "user_first_name"
        private const val KEY_USER_LAST_NAME = "user_last_name"
        private const val KEY_USER_PHONE = "user_phone"
        private const val KEY_ACCOUNT_TYPE = "account_type"
        private const val KEY_VENDOR_ID = "vendor_id"
    }

    fun saveTokens(accessToken: String, refreshToken: String, expiresAtEpochSeconds: Long) {
        settings.putString(KEY_ACCESS_TOKEN, accessToken)
        settings.putString(KEY_REFRESH_TOKEN, refreshToken)
        settings.putLong(KEY_ACCESS_EXPIRES_AT, expiresAtEpochSeconds)
    }

    /** Kept for backward compatibility with any single-token callers. */
    fun saveToken(token: String) {
        settings.putString(KEY_ACCESS_TOKEN, token)
    }

    fun getToken(): String? = settings.getStringOrNull(KEY_ACCESS_TOKEN)?.ifBlank { null }

    fun getRefreshToken(): String? = settings.getStringOrNull(KEY_REFRESH_TOKEN)?.ifBlank { null }

    fun getAccessExpiresAt(): Long = settings.getLong(KEY_ACCESS_EXPIRES_AT, 0L)

    fun saveUser(
        id: String,
        email: String,
        firstName: String,
        lastName: String,
        phone: String?,
        accountType: String,
        vendorId: String? = null
    ) {
        settings.putString(KEY_USER_ID, id)
        settings.putString(KEY_USER_EMAIL, email)
        settings.putString(KEY_USER_FIRST_NAME, firstName)
        settings.putString(KEY_USER_LAST_NAME, lastName)
        settings.putString(KEY_USER_PHONE, phone ?: "")
        settings.putString(KEY_ACCOUNT_TYPE, accountType)
        settings.putString(KEY_VENDOR_ID, vendorId ?: "")
    }

    fun getUserId(): String? = settings.getStringOrNull(KEY_USER_ID)?.ifBlank { null }
    fun getUserEmail(): String? = settings.getStringOrNull(KEY_USER_EMAIL)?.ifBlank { null }
    fun getUserFirstName(): String? = settings.getStringOrNull(KEY_USER_FIRST_NAME)?.ifBlank { null }
    fun getUserLastName(): String? = settings.getStringOrNull(KEY_USER_LAST_NAME)?.ifBlank { null }
    fun getUserPhone(): String? = settings.getStringOrNull(KEY_USER_PHONE)?.ifBlank { null }
    fun getAccountType(): String? = settings.getStringOrNull(KEY_ACCOUNT_TYPE)?.ifBlank { null }
    fun getVendorId(): String? = settings.getStringOrNull(KEY_VENDOR_ID)?.ifBlank { null }

    fun isLoggedIn(): Boolean = getToken() != null && getUserId() != null

    fun clear() {
        settings.clear()
    }
}
