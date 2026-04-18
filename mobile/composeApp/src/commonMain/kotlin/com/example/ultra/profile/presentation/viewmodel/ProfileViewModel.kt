package com.example.ultra.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.profile.presentation.intent.ProfileIntent
import com.example.ultra.profile.presentation.intent.ProfileState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {
    
    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    fun onAction(action: ProfileIntent) {
        when (action) {
            is ProfileIntent.CheckAuthState -> checkAuthState()
            is ProfileIntent.Logout -> logout()
        }
    }
    
    private fun checkAuthState() {
        val user = getCurrentUserUseCase()
        _state.update { it.copy(user = user, isLoggedIn = user != null) }
    }
    
    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _state.update { ProfileState() }
        }
    }
}
