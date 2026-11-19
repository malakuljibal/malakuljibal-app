package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.domain.model.AccessLevel
import com.example.domain.model.MedicalRecord

@Entity(tableName = "medical_records")
data class MedicalRecordEntity(
    @PrimaryKey val id: String,
    val familyId: String,
    val memberId: String,
    val consultationDate: String,
    val facility: String,
    val doctorName: String,
    val reportedSymptoms: String,
    val recordedDiagnosis: String,
    val prescribedSummary: String,
    val doctorRecommendations: String,
    val accessLevel: String
) {
    fun toDomain(): MedicalRecord {
        val acc = try {
            AccessLevel.valueOf(accessLevel)
        } catch (_: Exception) {
            AccessLevel.FULL_FAMILY
        }
        return MedicalRecord(
            id = id,
            memberId = memberId,
            consultationDate = consultationDate,
            facility = facility,
            doctorName = doctorName,
            reportedSymptoms = reportedSymptoms,
            recordedDiagnosis = recordedDiagnosis,
            prescribedSummary = prescribedSummary,
            doctorRecommendations = doctorRecommendations,
            accessLevel = acc
        )
    }

    companion object {
        fun fromDomain(record: MedicalRecord, familyId: String): MedicalRecordEntity {
            return MedicalRecordEntity(
                id = record.id,
                familyId = familyId,
                memberId = record.memberId,
                consultationDate = record.consultationDate,
                facility = record.facility,
                doctorName = record.doctorName,
                reportedSymptoms = record.reportedSymptoms,
                recordedDiagnosis = record.recordedDiagnosis,
                prescribedSummary = record.prescribedSummary,
                doctorRecommendations = record.doctorRecommendations,
                accessLevel = record.accessLevel.name
            )
        }
    }
}
