package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.local.entity.DocumentEntity
import com.example.data.local.entity.MedicalRecordEntity
import com.example.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE familyId = :familyId ORDER BY date DESC")
    fun getTransactions(familyId: String): Flow<List<TransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransactions(transactions: List<TransactionEntity>)

    @Query("DELETE FROM transactions WHERE familyId = :familyId")
    suspend fun clearTransactions(familyId: String)
}

@Dao
interface DocumentDao {
    @Query("SELECT * FROM documents WHERE familyId = :familyId ORDER BY issueDate DESC")
    fun getDocuments(familyId: String): Flow<List<DocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocuments(documents: List<DocumentEntity>)

    @Query("DELETE FROM documents WHERE id = :documentId")
    suspend fun deleteDocumentById(documentId: String)

    @Query("DELETE FROM documents WHERE familyId = :familyId")
    suspend fun clearDocuments(familyId: String)
}

@Dao
interface MedicalRecordDao {
    @Query("SELECT * FROM medical_records WHERE familyId = :familyId ORDER BY consultationDate DESC")
    fun getMedicalRecords(familyId: String): Flow<List<MedicalRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMedicalRecords(records: List<MedicalRecordEntity>)

    @Query("DELETE FROM medical_records WHERE familyId = :familyId")
    suspend fun clearMedicalRecords(familyId: String)
}
