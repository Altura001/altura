package com.example.ultra.core.data.util

// Math-based helper function (add this to your file or utilities)
fun Double.formatTwoDecimals(): String {
    val rounded = kotlin.math.round(this * 100) / 100
    val parts = rounded.toString().split(".")
    val cents = parts.getOrNull(1)?.padEnd(2, '0')?.take(2) ?: "00"
    return "${parts[0]}.$cents"
}
