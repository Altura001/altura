package com.example.ultra.core.data

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

class HttpClientFactory(private val engine: HttpClientEngine) {

	fun create(): HttpClient {
		return HttpClient(engine) {
			install(ContentNegotiation) {
				json(Json {
					prettyPrint = true
					isLenient = true
					ignoreUnknownKeys = true
					encodeDefaults = true
				})
			}

			install(Logging) {
				level = LogLevel.INFO
			}
			// Bound all requests so a dropped/unreachable backend fails with a typed
			// error instead of hanging the UI on an eternal spinner.
			install(HttpTimeout) {
				requestTimeoutMillis = 30_000
				connectTimeoutMillis = 10_000
				socketTimeoutMillis = 10_000
			}

			defaultRequest {
				contentType(ContentType.Application.Json)
			}
		}
	}
}
