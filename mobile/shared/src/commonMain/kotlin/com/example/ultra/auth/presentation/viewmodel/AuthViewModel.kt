package com.example.ultra.auth.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ultra.auth.domain.usecase.GetCurrentUserUseCase
import com.example.ultra.auth.domain.usecase.LoginUseCase
import com.example.ultra.auth.domain.usecase.LogoutUseCase
import com.example.ultra.auth.domain.usecase.SignupCustomerUseCase
import com.example.ultra.auth.domain.usecase.SignupVendorUseCase
import com.example.ultra.auth.domain.usecase.SocialAuthUseCase
import com.example.ultra.auth.presentation.intent.AuthAction
import com.example.ultra.auth.presentation.intent.SignupAccountType
import com.example.ultra.auth.presentation.intent.AuthEvent
import com.example.ultra.auth.presentation.intent.AuthMode
import com.example.ultra.auth.presentation.intent.AuthState
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.repository.CartRepository
import com.example.ultra.core.domain.util.onFailure
import com.example.ultra.core.domain.util.onSuccess
import com.example.ultra.core.presentation.UiText
import com.example.ultra.core.presentation.toUiText
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val signupCustomerUseCase: SignupCustomerUseCase,
    private val signupVendorUseCase: SignupVendorUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val socialAuthUseCase: SocialAuthUseCase,
    private val cartRepository: CartRepository
) : ViewModel() {
    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")


    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    private val _events = Channel<AuthEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        val user = getCurrentUserUseCase()
        if (user != null) {
            _state.update { it.copy(user = user, isLoggedIn = true) }
        }
    }

    fun onAction(action: AuthAction) {
        when (action) {
            is AuthAction.OnModeChange -> {
                _state.update {
                    it.copy(mode = action.mode, error = null)
                }
            }
            is AuthAction.OnFirstNameChange -> _state.update { it.copy(firstName = action.firstName) }
            is AuthAction.OnSurnameChange -> _state.update { it.copy(surname = action.surname) }
            is AuthAction.OnStoreNameChange -> _state.update { it.copy(storeName = action.storeName) }
            is AuthAction.OnEmailChange -> _state.update { it.copy(email = action.email) }
            is AuthAction.OnPhoneNumberChange -> _state.update { it.copy(phoneNumber = action.phoneNumber) }
            is AuthAction.OnDateOfBirthChange -> _state.update { it.copy(dateOfBirth = action.dateOfBirth) }
            is AuthAction.OnGenderChange -> _state.update { it.copy(gender = action.gender) }
            is AuthAction.OnAccountTypeChange -> _state.update {
                it.copy(accountType = action.accountType, error = null)
            }
            is AuthAction.OnPasswordChange -> _state.update { it.copy(password = action.password) }
            is AuthAction.OnConfirmPasswordChange -> _state.update { it.copy(confirmPassword = action.password) }
            is AuthAction.OnPasswordVisibilityToggle -> {
                _state.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
            }
            is AuthAction.OnConfirmPasswordVisibilityToggle -> {
                _state.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
            }
            is AuthAction.OnLoginClick -> login()
            is AuthAction.OnSignupClick -> signup()
            is AuthAction.OnGoogleSignIn -> googleSignIn(action.token)
            is AuthAction.OnAppleSignIn -> appleSignIn(action.authorizationCode)
            is AuthAction.OnLogout -> logout()
            is AuthAction.OnClearError -> _state.update { it.copy(error = null) }
        }
    }

    private fun login() {
        val snapshot = _state.value

        if (!emailRegex.matches(snapshot.email.trim())) {
            _state.update { it.copy(error = UiText.DynamicString("Enter a valid email address")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val loginAccountType =
                if (snapshot.accountType == SignupAccountType.Vendor) AuthAccountType.VENDOR
                else AuthAccountType.CUSTOMER

            loginUseCase(
                email = snapshot.email,
                password = snapshot.password,
                accountType = loginAccountType
            )
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
                    cartRepository.mergeLocalCartWithServer()
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun signup() {
        val snapshot = _state.value

        if (snapshot.firstName.isBlank() || snapshot.surname.isBlank()) {
            _state.update { it.copy(error = UiText.DynamicString("Enter your first name and surname")) }
            return
        }

        if (snapshot.accountType == SignupAccountType.Vendor && snapshot.storeName.isBlank()) {
            _state.update { it.copy(error = UiText.DynamicString("Enter your store name")) }
            return
        }

        if (snapshot.email.isBlank() || snapshot.phoneNumber.isBlank()) {
            _state.update { it.copy(error = UiText.DynamicString("Enter your email and phone number")) }
            return
        }

        if (!emailRegex.matches(snapshot.email.trim())) {
            _state.update { it.copy(error = UiText.DynamicString("Enter a valid email address")) }
            return
        }

        if (snapshot.phoneNumber.filter { char -> char.isDigit() }.length < 8) {
            _state.update { it.copy(error = UiText.DynamicString("Enter a valid phone number")) }
            return
        }

        if (snapshot.password.length < 6) {
            _state.update { it.copy(error = UiText.DynamicString("Password must be at least 6 characters")) }
            return
        }

        if (snapshot.password != snapshot.confirmPassword) {
            _state.update { it.copy(error = UiText.DynamicString("Passwords do not match")) }
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val signupResult = if (snapshot.accountType == SignupAccountType.Vendor) {
                signupVendorUseCase(
                    email = snapshot.email,
                    password = snapshot.password,
                    firstName = snapshot.firstName,
                    lastName = snapshot.surname,
                    phoneNumber = snapshot.phoneNumber,
                    storeName = snapshot.storeName
                )
            } else {
                signupCustomerUseCase(
                    email = snapshot.email,
                    password = snapshot.password,
                    firstName = snapshot.firstName,
                    lastName = snapshot.surname,
                    phoneNumber = snapshot.phoneNumber
                )
            }

            signupResult
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
                    cartRepository.mergeLocalCartWithServer()
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun googleSignIn(token: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            socialAuthUseCase.google(token)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
                    cartRepository.mergeLocalCartWithServer()
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun appleSignIn(authorizationCode: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            socialAuthUseCase.apple(authorizationCode)
                .onSuccess { user ->
                    _state.update { it.copy(isLoading = false, user = user, isLoggedIn = true) }
                    cartRepository.mergeLocalCartWithServer()
                    _events.send(AuthEvent.NavigateToHome)
                }
                .onFailure { error ->
                    _state.update { it.copy(isLoading = false, error = error.toUiText()) }
                }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            logoutUseCase()
            _state.update {
                AuthState(mode = AuthMode.Login)
            }
        }
    }
}
