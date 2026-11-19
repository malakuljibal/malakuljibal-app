package com.example.data.remote

import com.squareup.moshi.Json
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

data class LoginRequest(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

data class CreateTransactionRequest(
    @Json(name = "type") val type: String,
    @Json(name = "amount") val amount: Double,
    @Json(name = "description") val description: String,
    @Json(name = "transaction_date") val transactionDate: String
)

data class CreateHealthRecordRequest(
    @Json(name = "diagnosis") val diagnosis: String,
    @Json(name = "hospital_name") val hospitalName: String,
    @Json(name = "doctor_name") val doctorName: String,
    @Json(name = "notes") val notes: String,
    @Json(name = "record_date") val recordDate: String
)

data class CreateDocumentRequest(
    @Json(name = "title") val title: String,
    @Json(name = "category") val category: String,
    @Json(name = "document_date") val documentDate: String,
    @Json(name = "notes") val notes: String
)

data class CreateBabyGrowthRequest(
    @Json(name = "weight_kg") val weightKg: Double,
    @Json(name = "length_cm") val lengthCm: Double,
    @Json(name = "head_circumference_cm") val headCircumferenceCm: Double,
    @Json(name = "measurement_date") val measurementDate: String
)

/**
 * Complete Retrofit REST API client matching the live Malakul Jibal PHP 8.x backend routes
 * at https://api.malakuljibal.com.
 */
interface MalakulApiService {

    // Auth
    @POST("v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<ResponseBody>

    // 1. Finance
    @GET("v1/families/{family_id}/finance/summary")
    suspend fun getFinanceSummary(
        @Path("family_id") familyId: String
    ): Response<ResponseBody>

    @GET("v1/families/{family_id}/finance/transactions")
    suspend fun getTransactions(
        @Path("family_id") familyId: String
    ): Response<ResponseBody>

    @POST("v1/families/{family_id}/finance/transactions")
    suspend fun createTransaction(
        @Path("family_id") familyId: String,
        @Body request: CreateTransactionRequest
    ): Response<ResponseBody>

    @DELETE("v1/families/{family_id}/finance/transactions/{id}")
    suspend fun deleteTransaction(
        @Path("family_id") familyId: String,
        @Path("id") transactionId: String
    ): Response<ResponseBody>

    // 2. Health
    @GET("v1/families/{family_id}/health/records")
    suspend fun getHealthRecords(
        @Path("family_id") familyId: String
    ): Response<ResponseBody>

    @POST("v1/families/{family_id}/health/records")
    suspend fun createHealthRecord(
        @Path("family_id") familyId: String,
        @Body request: CreateHealthRecordRequest
    ): Response<ResponseBody>

    // 3. Documents
    @GET("v1/families/{family_id}/documents")
    suspend fun getDocuments(
        @Path("family_id") familyId: String
    ): Response<ResponseBody>

    @POST("v1/families/{family_id}/documents")
    suspend fun createDocument(
        @Path("family_id") familyId: String,
        @Body request: CreateDocumentRequest
    ): Response<ResponseBody>

    @DELETE("v1/families/{family_id}/documents/{id}")
    suspend fun deleteDocument(
        @Path("family_id") familyId: String,
        @Path("id") documentId: String
    ): Response<ResponseBody>

    // 4. Baby Growth
    @GET("v1/families/{family_id}/baby/growth/{baby_id}")
    suspend fun getBabyGrowth(
        @Path("family_id") familyId: String,
        @Path("baby_id") babyId: String
    ): Response<ResponseBody>

    @POST("v1/families/{family_id}/baby/growth/{baby_id}")
    suspend fun createBabyGrowth(
        @Path("family_id") familyId: String,
        @Path("baby_id") babyId: String,
        @Body request: CreateBabyGrowthRequest
    ): Response<ResponseBody>
}
