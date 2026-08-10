package com.example.ultra.profile.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.profile.presentation.intent.ProfileAction
import com.example.ultra.profile.presentation.intent.ProfileEvent
import com.example.ultra.profile.presentation.intent.ProfileState
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    private val _events = Channel<ProfileEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        val user = getCurrentUserUseCase()
        _state.update { it.copy(user = user, isLoggedIn = user != null) }
    }

    fun onAction(action: ProfileAction) {
        when (action) {
            is ProfileAction.Logout -> logout()
            is ProfileAction.GoToLogin -> navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        viewModelScope.launch {
            _events.send(ProfileEvent.NavigateToLogin)
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _state.update { ProfileState() }
            _events.send(ProfileEvent.NavigateToLogin)
        }
    }
}
