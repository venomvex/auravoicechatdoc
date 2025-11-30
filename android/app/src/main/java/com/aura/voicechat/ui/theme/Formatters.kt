package com.aura.voicechat.ui.theme

/**
 * Shared formatting utilities
 * Developer: Hawkaye Visions LTD — Pakistan
 */

/**
 * Formats a large number into a human-readable string with suffixes (K, M, B)
 * Examples: 1000 -> "1K", 1500000 -> "2M", 1000000000 -> "1B"
 */
fun formatNumber(number: Long): String {
    return when {
        number >= 1_000_000_000 -> String.format("%.0fB", number / 1_000_000_000.0)
        number >= 1_000_000 -> String.format("%.0fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.0fK", number / 1_000.0)
        else -> number.toString()
    }
}

/**
 * Formats a number with decimal places for more precision
 */
fun formatNumberWithDecimals(number: Long): String {
    return when {
        number >= 1_000_000_000 -> String.format("%.1fB", number / 1_000_000_000.0)
        number >= 1_000_000 -> String.format("%.1fM", number / 1_000_000.0)
        number >= 1_000 -> String.format("%.1fK", number / 1_000.0)
        else -> number.toString()
    }
}

/**
 * Formats coins/currency with proper display
 */
fun formatCoins(coins: Long): String {
    return when {
        coins >= 1_000_000_000 -> "${String.format("%.1f", coins / 1_000_000_000.0)}B"
        coins >= 1_000_000 -> "${String.format("%.1f", coins / 1_000_000.0)}M"
        coins >= 1_000 -> "${String.format("%.1f", coins / 1_000.0)}K"
        else -> coins.toString()
    }
}

/**
 * Formats a duration in seconds to MM:SS format
 */
fun formatDuration(seconds: Int): String {
    val minutes = seconds / 60
    val secs = seconds % 60
    return String.format("%02d:%02d", minutes, secs)
}

/**
 * Formats a timer countdown
 */
fun formatCountdown(seconds: Int): String {
    return "${seconds}s"
}
