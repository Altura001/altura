package com.example.ultra.core.domain.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val phone: String? = null,
    val avatarUrl: String? = null
) {
    val fullName: String get() = "$firstName $lastName"
}
