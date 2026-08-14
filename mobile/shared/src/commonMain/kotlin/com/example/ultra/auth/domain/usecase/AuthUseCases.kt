package com.example.ultra.auth.domain.usecase

import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.EmptyResult
import com.example.ultra.core.domain.util.Result

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        accountType: AuthAccountType = AuthAccountType.CUSTOMER
    ): Result<User, DataError.Network> {
        return repository.login(email, password, accountType)
    }
}

class SignupCustomerUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<User, DataError.Network> {
        return repository.signupCustomer(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber
        )
    }
}

class SignupVendorUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        storeName: String
    ): Result<User, DataError.Network> {
        return repository.signupVendor(
            email = email,
            password = password,
            firstName = firstName,
            lastName = lastName,
            phoneNumber = phoneNumber,
            storeName = storeName
        )
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(): EmptyResult<DataError.Network> {
        return repository.logout()
    }
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}

class SocialAuthUseCase(private val repository: AuthRepository) {
    suspend fun google(token: String): Result<User, DataError.Network> {
        return repository.googleAuth(token)
    }

    suspend fun apple(authorizationCode: String): Result<User, DataError.Network> {
        return repository.appleAuth(authorizationCode)
    }
}
