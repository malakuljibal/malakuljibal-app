package com.example.data.remote

import com.example.core.config.ApiConfig
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Builds an OkHttp + Retrofit client that injects 'Authorization: Bearer <jwt>' and
 * 'Content-Type: application/json' on every API request while redacting auth headers from logs.
 */
class RetrofitClientProvider(
    private val tokenProvider: () -> String?
) {
    private val moshi: Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val headerInterceptor = Interceptor { chain ->
        val original = chain.request()
        val builder = original.newBuilder()
            .header("Content-Type", "application/json")
            .header("Accept", "application/json")

        val token = tokenProvider()
        if (!token.isNullOrBlank() && !original.url.encodedPath.endsWith("/auth/login")) {
            builder.header("Authorization", "Bearer $token")
        }
        chain.proceed(builder.build())
    }

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
        redactHeader("Authorization")
        redactHeader("Cookie")
    }

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(headerInterceptor)
        .addInterceptor(loggingInterceptor)
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(25, TimeUnit.SECONDS)
        .writeTimeout(25, TimeUnit.SECONDS)
        .build()

    private var cachedBaseUrl: String? = null
    private var cachedService: MalakulApiService? = null

    fun getService(baseUrl: String = ApiConfig.DEFAULT_HOSTINGER_API_URL): MalakulApiService {
        val normalized = normalizeUrl(baseUrl)
        if (cachedService != null && cachedBaseUrl == normalized) {
            return cachedService!!
        }
        val service = Retrofit.Builder()
            .baseUrl(normalized)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(MalakulApiService::class.java)
        cachedBaseUrl = normalized
        cachedService = service
        return service
    }

    private fun normalizeUrl(raw: String): String {
        val trimmed = raw.trim().ifBlank { ApiConfig.DEFAULT_HOSTINGER_API_URL }
        val withScheme = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) {
            trimmed
        } else {
            "https://$trimmed"
        }
        return if (withScheme.endsWith("/")) withScheme else "$withScheme/"
    }
}
