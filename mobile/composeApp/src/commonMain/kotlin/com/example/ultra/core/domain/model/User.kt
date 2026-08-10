package com.example.ultra.core.domain.model

data class User(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val accountType: AuthAccountType = AuthAccountType.CUSTOMER,
    val phone: String? = null,
    val avatarUrl: String? = null
)
