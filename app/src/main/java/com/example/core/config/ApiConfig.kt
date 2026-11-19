package com.example.core.config

import com.example.BuildConfig

/**
 * Runtime API configuration for the live Hostinger PHP 8.x REST API + MariaDB backend.
 */
data class ApiConfig(
    val baseUrl: String = BuildConfig.API_BASE_URL,
    val useMockData: Boolean = BuildConfig.USE_MOCK_DATA,
    val isBackendConnected: Boolean = false,
    val syncStatusLabel: String = "Live API · Ready"
) {
    val normalizedBaseUrl: String
        get() {
            val trimmed = baseUrl.trim().ifBlank { DEFAULT_HOSTINGER_API_URL }
            return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        }

    companion object {
        const val DEFAULT_HOSTINGER_API_URL = "https://api.malakuljibal.com"
    }
}
