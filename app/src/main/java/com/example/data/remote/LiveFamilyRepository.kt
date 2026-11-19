package com.example.data.remote

import com.example.data.local.AuthSession
import com.example.domain.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Live REST API repository connecting the Android frontend to https://api.malakuljibal.com.
 * Handles network errors, JSON parsing, and HTTP 401 expired-token detection.
 */
class LiveFamilyRepository(
    private val clientProvider: RetrofitClientProvider
) {

    suspend fun login(
        baseUrl: String,
        email: String,
        password: String
    ): ApiResult<AuthSession> = safeCall(isAuthEndpoint = true) {
        val response = clientProvider.getService(baseUrl).login(LoginRequest(email.trim(), password))
        handleResponse(response, isAuthEndpoint = true) { body ->
            ApiResponseParser.parseLoginSession(body, email.trim())
                ?: throw IllegalStateException("Login succeeded but no JWT token was returned by the server.")
        }
    }

    suspend fun fetchFinanceSummary(
        baseUrl: String,
        familyId: String,
        fallback: FinancialSummary
    ): ApiResult<FinancialSummary> = safeCall {
        val response = clientProvider.getService(baseUrl).getFinanceSummary(familyId)
        handleResponse(response) { body ->
            ApiResponseParser.parseFinanceSummary(body, fallback)
        }
    }

    suspend fun fetchTransactions(
        baseUrl: String,
        familyId: String,
        activeUserId: String,
        activeUserName: String
    ): ApiResult<List<TransactionItem>> = safeCall {
        val response = clientProvider.getService(baseUrl).getTransactions(familyId)
        handleResponse(response) { body ->
            ApiResponseParser.parseTransactions(body, activeUserId, activeUserName)
        }
    }

    suspend fun createTransaction(
        baseUrl: String,
        familyId: String,
        type: String,
        amount: Double,
        description: String,
        transactionDate: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).createTransaction(
            familyId = familyId,
            request = CreateTransactionRequest(
                type = type,
                amount = amount,
                description = description,
                transactionDate = transactionDate
            )
        )
        handleResponse(response) { Unit }
    }

    suspend fun deleteTransaction(
        baseUrl: String,
        familyId: String,
        transactionId: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).deleteTransaction(familyId, transactionId)
        handleResponse(response) { Unit }
    }

    suspend fun fetchHealthRecords(
        baseUrl: String,
        familyId: String,
        defaultMemberId: String
    ): ApiResult<List<MedicalRecord>> = safeCall {
        val response = clientProvider.getService(baseUrl).getHealthRecords(familyId)
        handleResponse(response) { body ->
            ApiResponseParser.parseHealthRecords(body, defaultMemberId)
        }
    }

    suspend fun createHealthRecord(
        baseUrl: String,
        familyId: String,
        diagnosis: String,
        hospitalName: String,
        doctorName: String,
        notes: String,
        recordDate: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).createHealthRecord(
            familyId = familyId,
            request = CreateHealthRecordRequest(
                diagnosis = diagnosis,
                hospitalName = hospitalName,
                doctorName = doctorName,
                notes = notes,
                recordDate = recordDate
            )
        )
        handleResponse(response) { Unit }
    }

    suspend fun fetchDocuments(
        baseUrl: String,
        familyId: String,
        defaultOwnerId: String,
        defaultOwnerName: String
    ): ApiResult<List<FamilyDocument>> = safeCall {
        val response = clientProvider.getService(baseUrl).getDocuments(familyId)
        handleResponse(response) { body ->
            ApiResponseParser.parseDocuments(body, defaultOwnerId, defaultOwnerName)
        }
    }

    suspend fun createDocument(
        baseUrl: String,
        familyId: String,
        title: String,
        category: String,
        documentDate: String,
        notes: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).createDocument(
            familyId = familyId,
            request = CreateDocumentRequest(
                title = title,
                category = category,
                documentDate = documentDate,
                notes = notes
            )
        )
        handleResponse(response) { Unit }
    }

    suspend fun uploadDocumentFile(
        baseUrl: String,
        familyId: String,
        title: String,
        category: String,
        documentDate: String,
        notes: String?,
        filePart: MultipartBody.Part
    ): ApiResult<Unit> = safeCall {
        val titleBody = title.toRequestBody(MultipartBody.FORM)
        val categoryBody = category.toRequestBody(MultipartBody.FORM)
        val dateBody = documentDate.toRequestBody(MultipartBody.FORM)
        val notesBody = notes?.toRequestBody(MultipartBody.FORM)

        val response = clientProvider.getService(baseUrl).uploadDocument(
            familyId = familyId,
            title = titleBody,
            category = categoryBody,
            documentDate = dateBody,
            notes = notesBody,
            file = filePart
        )
        handleResponse(response) { Unit }
    }

    suspend fun deleteDocument(
        baseUrl: String,
        familyId: String,
        documentId: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).deleteDocument(familyId, documentId)
        handleResponse(response) { Unit }
    }

    suspend fun fetchBabyGrowth(
        baseUrl: String,
        familyId: String,
        babyId: String
    ): ApiResult<List<GrowthMeasurement>> = safeCall {
        val response = clientProvider.getService(baseUrl).getBabyGrowth(familyId, babyId)
        handleResponse(response) { body ->
            ApiResponseParser.parseBabyGrowth(body, babyId)
        }
    }

    suspend fun createBabyGrowth(
        baseUrl: String,
        familyId: String,
        babyId: String,
        weightKg: Double,
        lengthCm: Double,
        headCircumferenceCm: Double,
        measurementDate: String
    ): ApiResult<Unit> = safeCall {
        val response = clientProvider.getService(baseUrl).createBabyGrowth(
            familyId = familyId,
            babyId = babyId,
            request = CreateBabyGrowthRequest(
                weightKg = weightKg,
                lengthCm = lengthCm,
                headCircumferenceCm = headCircumferenceCm,
                measurementDate = measurementDate
            )
        )
        handleResponse(response) { Unit }
    }

    private suspend fun <T> safeCall(
        isAuthEndpoint: Boolean = false,
        block: suspend () -> ApiResult<T>
    ): ApiResult<T> = withContext(Dispatchers.IO) {
        try {
            block()
        } catch (_: UnknownHostException) {
            ApiResult.Error("Unable to reach server. Please check your internet connection.")
        } catch (_: SocketTimeoutException) {
            ApiResult.Error("Server request timed out. Please try again.")
        } catch (e: IOException) {
            ApiResult.Error("Network error: ${e.localizedMessage ?: "Connection interrupted"}")
        } catch (e: Exception) {
            ApiResult.Error(e.localizedMessage ?: "Unexpected error while communicating with API.")
        }
    }

    private fun <T> handleResponse(
        response: Response<ResponseBody>,
        isAuthEndpoint: Boolean = false,
        parser: (String) -> T
    ): ApiResult<T> {
        val code = response.code()
        if (response.isSuccessful) {
            val rawBody = response.body()?.string().orEmpty()
            return try {
                ApiResult.Success(parser(rawBody))
            } catch (e: Exception) {
                ApiResult.Error("Failed to parse server response: ${e.localizedMessage ?: "Invalid JSON"}")
            }
        }

        val errBody = try {
            response.errorBody()?.string()
        } catch (_: Exception) {
            null
        }
        val serverMsg = ApiResponseParser.extractErrorMessage(
            errBody,
            fallback = when (code) {
                401 -> if (isAuthEndpoint) "Invalid email or password." else "Your session has expired. Please sign in again."
                403 -> "Access denied for this family record."
                404 -> "Requested resource was not found on the server."
                422 -> "Please check the submitted fields and try again."
                in 500..599 -> "Server error ($code). Please try again shortly."
                else -> "Request failed with HTTP $code."
            }
        )

        if (code == 401 && !isAuthEndpoint) {
            return ApiResult.Unauthorized(serverMsg)
        }
        if (code == 403 && !isAuthEndpoint && serverMsg.contains("token", ignoreCase = true) && serverMsg.contains("expir", ignoreCase = true)) {
            return ApiResult.Unauthorized(serverMsg)
        }
        return ApiResult.Error(serverMsg, httpCode = code)
    }
}
