package com.example.data.remote

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Unauthorized(val message: String) : ApiResult<Nothing>()
    data class Error(val message: String, val httpCode: Int? = null) : ApiResult<Nothing>()
}
