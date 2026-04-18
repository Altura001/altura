package com.example.ultra.auth.presentation.intent

import com.example.ultra.core.domain.model.User

data class AuthState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null,
    val isLoggedIn: Boolean = false
)

sealed interface AuthIntent {
    data class Login(val email: String, val password: String) : AuthIntent
    data class GoogleSignIn(val token: String) : AuthIntent
    data class AppleSignIn(val authorizationCode: String) : AuthIntent
    data object Logout : AuthIntent
    data object CheckAuthState : AuthIntent
    data object ClearError : AuthIntent
}
