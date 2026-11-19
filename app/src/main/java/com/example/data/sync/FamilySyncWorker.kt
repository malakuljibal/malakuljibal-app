package com.example.data.sync

import android.content.Context
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.data.local.AppDatabase
import com.example.data.local.SessionManager
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.MedicalRecordEntity
import com.example.data.local.entity.TransactionEntity
import com.example.data.remote.ApiResult
import com.example.data.remote.LiveFamilyRepository
import com.example.data.remote.RetrofitClientProvider
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.concurrent.TimeUnit

class FamilySyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        val sessionManager = SessionManager(applicationContext)
        val session = sessionManager.loadSession() ?: return@coroutineScope Result.success()
        val baseUrl = sessionManager.getApiBaseUrl()
        val familyId = session.familyId

        val clientProvider = RetrofitClientProvider(
            tokenProvider = { sessionManager.getToken() },
            onUnauthorized = { sessionManager.clearSession() }
        )
        val repository = LiveFamilyRepository(clientProvider)
        val database = AppDatabase.getDatabase(applicationContext)

        try {
            val txDeferred = async {
                repository.fetchTransactions(baseUrl, familyId, session.userId, session.fullName)
            }
            val docsDeferred = async {
                repository.fetchDocuments(baseUrl, familyId, session.userId, session.fullName)
            }
            val healthDeferred = async {
                repository.fetchHealthRecords(baseUrl, familyId, session.userId)
            }

            val txRes = txDeferred.await()
            val docsRes = docsDeferred.await()
            val healthRes = healthDeferred.await()

            if (txRes is ApiResult.Success) {
                database.transactionDao().insertTransactions(
                    txRes.data.map { TransactionEntity.fromDomain(it, familyId) }
                )
            }
            if (docsRes is ApiResult.Success) {
                database.documentDao().insertDocuments(
                    docsRes.data.map { DocumentEntity.fromDomain(it, familyId) }
                )
            }
            if (healthRes is ApiResult.Success) {
                database.medicalRecordDao().insertMedicalRecords(
                    healthRes.data.map { MedicalRecordEntity.fromDomain(it, familyId) }
                )
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val SYNC_WORK_NAME = "FamilyPeriodicSyncWork"

        fun schedulePeriodicSync(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val syncRequest = PeriodicWorkRequestBuilder<FamilySyncWorker>(
                15, TimeUnit.MINUTES
            )
                .setConstraints(constraints)
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                SYNC_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                syncRequest
            )
        }

        fun cancelPeriodicSync(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(SYNC_WORK_NAME)
        }
    }
}
