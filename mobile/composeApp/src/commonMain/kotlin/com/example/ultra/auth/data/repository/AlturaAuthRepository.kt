package com.example.ultra.auth.data.repository

import com.example.ultra.core.data.AlturaApiService
import com.example.ultra.core.data.TokenStorage
import com.example.ultra.core.data.toUser
import com.example.ultra.core.data.util.safeApiCall
import com.example.ultra.core.domain.model.AuthAccountType
import com.example.ultra.core.domain.model.User
import com.example.ultra.core.domain.repository.AuthRepository
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.EmptyResult
import com.example.ultra.core.domain.util.Result
import com.example.ultra.core.domain.util.map
import com.example.ultra.core.domain.util.onSuccess

/** AuthRepository backed by the Altura Nova .NET backend (JWT + refresh tokens). */
class AlturaAuthRepository(
    private val api: AlturaApiService,
    private val tokenStorage: TokenStorage
) : AuthRepository {

    private var currentUser: User? = null

    init {
        loadSavedSession()
    }

    private fun loadSavedSession() {
        if (tokenStorage.isLoggedIn()) {
            currentUser = User(
                id = tokenStorage.getUserId() ?: "",
                email = tokenStorage.getUserEmail() ?: "",
                firstName = tokenStorage.getUserFirstName() ?: "",
                lastName = tokenStorage.getUserLastName() ?: "",
                accountType = when (tokenStorage.getAccountType()) {
                    "VENDOR" -> AuthAccountType.VENDOR
                    else -> AuthAccountType.CUSTOMER
                },
                phone = tokenStorage.getUserPhone()
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String,
        accountType: AuthAccountType
    ): Result<User, DataError.Network> =
        safeApiCall { api.login(email.trim(), password) }
            .map { persist(it) }

    override suspend fun signupCustomer(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String
    ): Result<User, DataError.Network> =
        safeApiCall {
            api.registerCustomer(email.trim(), password, firstName.trim(), lastName.trim(), phoneNumber)
        }.map { persist(it) }

    override suspend fun signupVendor(
        email: String,
        password: String,
        firstName: String,
        lastName: String,
        phoneNumber: String,
        storeName: String
    ): Result<User, DataError.Network> =
        safeApiCall {
            api.registerVendor(email.trim(), password, firstName.trim(), lastName.trim(), storeName.trim(), phoneNumber)
        }.map { persist(it) }

    override suspend fun logout(): EmptyResult<DataError.Network> {
        val refresh = tokenStorage.getRefreshToken()
        if (refresh != null) {
            safeApiCall { api.logout(refresh) } // best-effort
        }
        currentUser = null
        tokenStorage.clear()
        return Result.Success(Unit)
    }

    override fun getCurrentUser(): User? = currentUser

    override suspend fun googleAuth(token: String): Result<User, DataError.Network> =
        Result.Error(DataError.Network.UNKNOWN)

    override suspend fun appleAuth(authorizationCode: String): Result<User, DataError.Network> =
        Result.Error(DataError.Network.UNKNOWN)

    override fun isLoggedIn(): Boolean = currentUser != null || tokenStorage.isLoggedIn()

    private fun persist(auth: com.example.ultra.core.data.AuthResponse): User {
        tokenStorage.saveTokens(auth.accessToken, auth.refreshToken, 0L)
        val user = auth.user.toUser()
        tokenStorage.saveUser(
            id = user.id,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            phone = user.phone,
            accountType = user.accountType.name,
            vendorId = auth.user.vendorId
        )
        currentUser = user
        return user
    }
}
