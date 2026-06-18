package com.example.ultra

object AppConfig {
    // Android emulator uses 10.0.2.2 to reach host
    // iOS simulator uses localhost
    const val MEDUSA_BACKEND_URL = "http://10.0.2.2:9000"
    const val MEDUSA_BACKEND_URL_IOS = "http://localhost:9000"
    const val MEDUSA_PUBLISHABLE_KEY = "pk_test_dummy" // Any key works for dev
}