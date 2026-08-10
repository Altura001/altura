package com.example.ultra.auth.presentation.intent

import com.example.ultra.core.domain.model.User
import com.example.ultra.core.presentation.UiText

data class AuthState(
    val mode: AuthMode = AuthMode.Login,
    val accountType: SignupAccountType = SignupAccountType.Customer,
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: UiText? = null,
    val firstName: String = "",
    val surname: String = "",
    val storeName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoggedIn: Boolean = false
)

enum class AuthMode {
    Login,
    Signup
}

enum class SignupAccountType {
    Customer,
    Vendor
}

sealed interface AuthAction {
    data class OnModeChange(val mode: AuthMode) : AuthAction
    data class OnFirstNameChange(val firstName: String) : AuthAction
    data class OnSurnameChange(val surname: String) : AuthAction
    data class OnStoreNameChange(val storeName: String) : AuthAction
    data class OnEmailChange(val email: String) : AuthAction
    data class OnPhoneNumberChange(val phoneNumber: String) : AuthAction
    data class OnDateOfBirthChange(val dateOfBirth: String) : AuthAction
    data class OnGenderChange(val gender: String) : AuthAction
    data class OnAccountTypeChange(val accountType: SignupAccountType) : AuthAction
    data class OnPasswordChange(val password: String) : AuthAction
    data class OnConfirmPasswordChange(val password: String) : AuthAction
    data object OnPasswordVisibilityToggle : AuthAction
    data object OnConfirmPasswordVisibilityToggle : AuthAction
    data object OnLoginClick : AuthAction
    data object OnSignupClick : AuthAction
    data class OnGoogleSignIn(val token: String) : AuthAction
    data class OnAppleSignIn(val authorizationCode: String) : AuthAction
    data object OnLogout : AuthAction
    data object OnClearError : AuthAction
}

sealed interface AuthEvent {
    data object NavigateToHome : AuthEvent
}
