package com.example.ultra.auth.domain.usecase

import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.repository.AuthRepository

class LoginUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke(email: String, password: String): User {
        return repository.login(email, password)
    }
}

class LogoutUseCase(private val repository: AuthRepository) {
    suspend operator fun invoke() {
        repository.logout()
    }
}

class GetCurrentUserUseCase(private val repository: AuthRepository) {
    operator fun invoke(): User? {
        return repository.getCurrentUser()
    }
}

class SocialAuthUseCase(private val repository: AuthRepository) {
    suspend fun google(token: String): User {
        return repository.googleAuth(token)
    }
    
    suspend fun apple(authorizationCode: String): User {
        return repository.appleAuth(authorizationCode)
    }
}
