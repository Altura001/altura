package com.example.ultra.core.data.util

import com.example.ultra.core.data.ApiException
import com.example.ultra.core.domain.util.DataError
import com.example.ultra.core.domain.util.Result
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException

suspend fun <T> safeApiCall(block: suspend () -> T): Result<T, DataError.Network> {
	return try {
		Result.Success(block())
	} catch (e: CancellationException) {
		throw e
	} catch (e: SerializationException) {
		Result.Error(DataError.Network.SERIALIZATION)
	} catch (e: HttpRequestTimeoutException) {
		Result.Error(DataError.Network.REQUEST_TIMEOUT)
	} catch (e: kotlinx.io.IOException) {
		Result.Error(DataError.Network.NO_INTERNET)
	} catch (e: ApiException) {
		Result.Error(e.toDataError())
	} catch (e: Exception) {
		println(e)
		Result.Error(DataError.Network.UNKNOWN)
	}
}

fun ApiException.toDataError(): DataError.Network = when (statusCode) {
	400 -> DataError.Network.BAD_REQUEST
	401 -> DataError.Network.UNAUTHORIZED
	403 -> DataError.Network.FORBIDDEN
	404 -> DataError.Network.NOT_FOUND
	408 -> DataError.Network.REQUEST_TIMEOUT
	409 -> DataError.Network.CONFLICT
	429 -> DataError.Network.TOO_MANY_REQUESTS
	in 500..599 -> DataError.Network.SERVER
	else -> DataError.Network.UNKNOWN
}
