package com.example.ultra.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LoginUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.auth.domain.usecase.SocialAuthUseCase
import com.example.ultra.auth.presentation.intent.AuthIntent
import com.example.ultra.auth.presentation.intent.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val socialAuthUseCase: SocialAuthUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    fun onAction(action: AuthIntent) {
        when (action) {
            is AuthIntent.Login -> login(action.email, action.password)
            is AuthIntent.GoogleSignIn -> googleSignIn(action.token)
            is AuthIntent.AppleSignIn -> appleSignIn(action.authorizationCode)
            is AuthIntent.Logout -> logout()
            is AuthIntent.CheckAuthState -> checkAuthState()
            is AuthIntent.ClearError -> clearError()
        }
    }
    
    private fun login(email: String, password: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = loginUseCase(email, password)
                _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun googleSignIn(token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = socialAuthUseCase.google(token)
                _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun appleSignIn(authorizationCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            try {
                val user = socialAuthUseCase.apple(authorizationCode)
                _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
            } catch (e: Exception) {
                _state.update { it.copy(isLoading = false, error = e.message) }
            }
        }
    }
    
    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _state.update { AuthState() }
        }
    }
    
    private fun checkAuthState() {
        val user = getCurrentUserUseCase()
        _state.update { it.copy(user = user, isLoggedIn = user != null) }
    }
    
    private fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
