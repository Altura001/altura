package com.example.ultra.profile.presentation.intent

import com.example.ultra.core.domain.model.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val isLoggedIn: Boolean = false
)

sealed interface ProfileAction {
    data object Logout : ProfileAction
    data object GoToLogin : ProfileAction
}

sealed interface ProfileEvent {
    data object NavigateToLogin : ProfileEvent
}
