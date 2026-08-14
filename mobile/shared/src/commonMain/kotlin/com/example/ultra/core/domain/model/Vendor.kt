package com.example.ultra.core.domain.model

data class Vendor(
    val id: String,
    val name: String,
    val description: String,
    val logoUrl: String? = null,
    val bannerUrl: String? = null
)
