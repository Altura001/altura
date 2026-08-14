package com.example.ultra.core.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class BirdImage(
	val author: String,
	val category: String,
	val path: String
)