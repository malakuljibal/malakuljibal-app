package com.example.domain.model

enum class MeasurementType(val label: String, val unit: String) {
    BLOOD_PRESSURE("Blood Pressure", "mmHg"),
    WEIGHT("Weight", "kg"),
    HEIGHT("Height", "cm"),
    TEMPERATURE("Temperature", "°C"),
    BLOOD_GLUCOSE("Blood Glucose", "mg/dL")
}

data class MedicalProfile(
    val memberId: String,
    val bloodType: String,
    val allergies: List<String>,
    val conditions: List<String>,
    val relevantHistory: String,
    val emergencyContactName: String,
    val emergencyContactPhone: String,
    val primaryPhysician: String,
    val accessLevel: AccessLevel = AccessLevel.PRIVATE_PERSONAL,
    val authorizedViewerIds: List<String> = emptyList()
)

data class HealthMeasurement(
    val id: String,
    val memberId: String,
    val type: MeasurementType,
    val valueDisplay: String,
    val numericValue: Float,
    val dateLabel: String,
    val note: String
)

data class MedicalRecord(
    val id: String,
    val memberId: String,
    val consultationDate: String,
    val facility: String,
    val doctorName: String,
    val reportedSymptoms: String,
    val recordedDiagnosis: String,
    val prescribedSummary: String,
    val doctorRecommendations: String,
    val accessLevel: AccessLevel
)

data class MedicationItem(
    val id: String,
    val memberId: String,
    val name: String,
    val dosage: String,
    val frequency: String,
    val startDate: String,
    val endDate: String?,
    val prescribingDoctor: String,
    val notes: String,
    val isActive: Boolean
)

data class LabResultPreview(
    val id: String,
    val memberId: String,
    val testName: String,
    val category: String,
    val examDate: String,
    val labFacility: String,
    val recordedValue: String,
    val labProvidedRange: String,
    val attachmentType: String
)

data class HealthAppointment(
    val id: String,
    val memberId: String,
    val memberName: String,
    val title: String,
    val facility: String,
    val doctorName: String,
    val dateTimeLabel: String,
    val accessLevel: AccessLevel
)
