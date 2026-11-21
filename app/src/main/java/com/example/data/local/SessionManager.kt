package com.example.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import com.example.core.config.ApiConfig
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

data class AuthSession(
    val token: String,
    val userId: String,
    val familyId: String,
    val fullName: String,
    val email: String,
    val role: String
)

/**
 * Local session persistence storing JWT token and user metadata (including family_id).
 * Encrypts the JWT token at rest using Android Keystore AES-256-GCM.
 */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun saveSession(session: AuthSession) {
        val encryptedToken = encryptToken(session.token)
        prefs.edit()
            .putString(KEY_JWT_ENC, encryptedToken)
            .putString(KEY_USER_ID, session.userId)
            .putString(KEY_FAMILY_ID, session.familyId)
            .putString(KEY_FULL_NAME, session.fullName)
            .putString(KEY_EMAIL, session.email)
            .putString(KEY_ROLE, session.role)
            .apply()
    }

    fun loadSession(): AuthSession? {
        val encrypted = prefs.getString(KEY_JWT_ENC, null) ?: return null
        val token = decryptToken(encrypted) ?: return null
        if (token.isBlank()) return null
        val familyId = prefs.getString(KEY_FAMILY_ID, "").orEmpty()
        if (familyId.isBlank()) return null
        return AuthSession(
            token = token,
            userId = prefs.getString(KEY_USER_ID, "1").orEmpty().ifBlank { "1" },
            familyId = familyId,
            fullName = prefs.getString(KEY_FULL_NAME, "Family Member").orEmpty().ifBlank { "Family Member" },
            email = prefs.getString(KEY_EMAIL, "").orEmpty(),
            role = prefs.getString(KEY_ROLE, "SUPER_ADMIN").orEmpty().ifBlank { "SUPER_ADMIN" }
        )
    }

    fun getToken(): String? {
        val encrypted = prefs.getString(KEY_JWT_ENC, null) ?: return null
        return decryptToken(encrypted)?.takeIf { it.isNotBlank() }
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_JWT_ENC)
            .remove(KEY_USER_ID)
            .remove(KEY_FAMILY_ID)
            .remove(KEY_FULL_NAME)
            .remove(KEY_EMAIL)
            .remove(KEY_ROLE)
            .apply()
    }

    fun saveApiBaseUrl(url: String) {
        prefs.edit().putString(KEY_API_BASE_URL, url).apply()
    }

    fun getAppLanguage(): String {
        return prefs.getString(KEY_APP_LANGUAGE, "id") ?: "id"
    }

    fun saveAppLanguage(languageCode: String) {
        prefs.edit().putString(KEY_APP_LANGUAGE, languageCode).apply()
    }

    fun getApiBaseUrl(): String {
        return prefs.getString(KEY_API_BASE_URL, ApiConfig.DEFAULT_HOSTINGER_API_URL)
            ?.trim()
            ?.takeIf { it.isNotBlank() }
            ?: ApiConfig.DEFAULT_HOSTINGER_API_URL
    }

    private fun getOrCreateSecretKey(): SecretKey? {
        return try {
            val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
            val existing = keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            if (existing != null) return existing

            val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE)
            val spec = KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
            keyGenerator.init(spec)
            keyGenerator.generateKey()
        } catch (_: Exception) {
            null
        }
    }

    private fun encryptToken(plainText: String): String {
        return try {
            val key = getOrCreateSecretKey() ?: return PLAIN_PREFIX + plainText
            val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, key)
            val iv = cipher.iv
            val encrypted = cipher.doFinal(plainText.toByteArray(Charsets.UTF_8))
            val ivB64 = Base64.encodeToString(iv, Base64.NO_WRAP)
            val dataB64 = Base64.encodeToString(encrypted, Base64.NO_WRAP)
            "$ENC_PREFIX$ivB64:$dataB64"
        } catch (_: Exception) {
            PLAIN_PREFIX + plainText
        }
    }

    private fun decryptToken(stored: String): String? {
        return try {
            if (stored.startsWith(PLAIN_PREFIX)) {
                return stored.removePrefix(PLAIN_PREFIX)
            }
            if (!stored.startsWith(ENC_PREFIX)) return null
            val payload = stored.removePrefix(ENC_PREFIX)
            val parts = payload.split(":")
            if (parts.size != 2) return null
            val key = getOrCreateSecretKey() ?: return null
            val iv = Base64.decode(parts[0], Base64.NO_WRAP)
            val data = Base64.decode(parts[1], Base64.NO_WRAP)
            val cipher = Cipher.getInstance(AES_GCM_TRANSFORMATION)
            cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))
            String(cipher.doFinal(data), Charsets.UTF_8)
        } catch (_: Exception) {
            null
        }
    }

    companion object {
        private const val PREFS_NAME = "malakul_jibal_secure_session"
        private const val ANDROID_KEYSTORE = "AndroidKeyStore"
        private const val KEY_ALIAS = "malakul_jibal_jwt_aes_key"
        private const val AES_GCM_TRANSFORMATION = "AES/GCM/NoPadding"
        private const val ENC_PREFIX = "enc_v1:"
        private const val PLAIN_PREFIX = "plain_v1:"

        private const val KEY_JWT_ENC = "jwt_token_enc"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_FAMILY_ID = "family_id"
        private const val KEY_FULL_NAME = "full_name"
        private const val KEY_EMAIL = "email"
        private const val KEY_ROLE = "role"
        private const val KEY_API_BASE_URL = "api_base_url"
        private const val KEY_APP_LANGUAGE = "app_language"
    }
}
