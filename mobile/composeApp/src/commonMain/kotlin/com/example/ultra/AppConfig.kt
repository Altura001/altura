package com.example.ultra

object AppConfig {
    // Android emulator uses 10.0.2.2 to reach the host; iOS simulator uses localhost.

    // Altura Nova .NET backend (primary backend for the app).
    const val ALTURA_BACKEND_URL = "http://10.0.2.2:8080"
    const val ALTURA_BACKEND_URL_IOS = "http://localhost:8080"

    // Legacy Medusa backend (kept for reference; no longer used by the app).
    const val MEDUSA_BACKEND_URL = "http://10.0.2.2:9000"
    const val MEDUSA_BACKEND_URL_IOS = "http://localhost:9000"
    const val MEDUSA_PUBLISHABLE_KEY = "pk_e311755335a99e1756902a3880d6814d2e9187bb3c83c19dc328590b7fc6aef6"
}
