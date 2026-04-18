package com.example.ultra.auth.data.repository

import com.example.ultra.core.data.MedusaApiService
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.repository.AuthRepository

class DefaultAuthRepository(
    private val apiService: MedusaApiService
) : AuthRepository {
    private var currentUser: User? = null
    private var accessToken: String? = null
    
    override suspend fun login(email: String, password: String): User {
        return try {
            val response = apiService.login(email, password)
            val customer = response.customer
            if (customer != null) {
                accessToken = response.access_token
                User(
                    id = customer.id,
                    email = customer.email,
                    firstName = customer.first_name,
                    lastName = customer.last_name
                ).also { currentUser = it }
            } else {
                throw Exception("Login failed")
            }
        } catch (e: Exception) {
            throw Exception("Login failed: ${e.message}")
        }
    }
    
    override suspend fun logout() {
        currentUser = null
        accessToken = null
    }
    
    override fun getCurrentUser(): User? = currentUser
    
    override suspend fun googleAuth(token: String): User {
        throw NotImplementedError("Google auth not implemented in Medusa")
    }
    
    override suspend fun appleAuth(authorizationCode: String): User {
        throw NotImplementedError("Apple auth not implemented in Medusa")
    }
    
    override fun isLoggedIn(): Boolean = currentUser != null
}