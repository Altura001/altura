package com.example.ultra

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform