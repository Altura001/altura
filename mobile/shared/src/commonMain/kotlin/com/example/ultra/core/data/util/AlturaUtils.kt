package com.example.ultra.core.data.util

import kotlinx.serialization.json.Json

// Math-based helper function (add this to your file or utilities)
fun Double.formatTwoDecimals(): String {
    val rounded = kotlin.math.round(this * 100) / 100
    val parts = rounded.toString().split(".")
    val cents = parts.getOrNull(1)?.padEnd(2, '0')?.take(2) ?: "00"
    return "${parts[0]}.$cents"
}

fun Double.formatCurrency(): String {
    val rounded = kotlin.math.round(this).toLong()
    return rounded.toString().reversed().chunked(3).joinToString(",").reversed()
}

inline fun <reified T> T.toJsonString(): String {
    return Json.encodeToString(this)
}