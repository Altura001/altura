package com.example.ultra

object AppConfig {
    // Android emulator uses 10.0.2.2 to reach host
    // iOS simulator uses localhost (or your machine's IP for physical device)
    const val MEDUSA_BACKEND_URL = "http://10.0.2.2:9000"
    const val MEDUSA_BACKEND_URL_IOS = "http://localhost:9000"
    const val MEDUSA_PUBLISHABLE_KEY = "pk_6b73098a19dabfd0f432be54e1b4d98b9d3f0fb74e4c2fcdcb8f83046f9b7800"
}