package com.example.ui.i18n

enum class AppLanguage(val code: String, val displayName: String, val flag: String) {
    INDONESIAN("id", "Indonesia", "🇮🇩"),
    ENGLISH("en", "English", "🇬🇧");

    companion object {
        fun fromCode(code: String?): AppLanguage {
            return entries.firstOrNull { it.code.equals(code, ignoreCase = true) } ?: INDONESIAN
        }
    }
}
