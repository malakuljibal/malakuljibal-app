package com.example.domain.model

enum class MilestoneDomain(val label: String) {
    GROSS_MOTOR("Motorik Kasar"),
    FINE_MOTOR("Motorik Halus"),
    LANGUAGE("Bahasa & Komunikasi"),
    COGNITIVE("Kognitif"),
    SOCIAL_EMOTIONAL("Sosial & Emosional")
}

data class ChildProfile(
    val memberId: String,
    val fullName: String,
    val dateOfBirth: String,
    val ageSummary: String,
    val gender: String,
    val birthWeightKg: Float,
    val birthLengthCm: Float,
    val birthHeadCircumferenceCm: Float,
    val birthHistoryNote: String,
    val latestWeightKg: Float,
    val latestLengthCm: Float,
    val latestHeadCircumferenceCm: Float,
    val lastMeasuredDate: String,
    val nextImmunizationSummary: String
)

data class GrowthMeasurement(
    val id: String,
    val childMemberId: String,
    val ageMonths: Int,
    val dateLabel: String,
    val weightKg: Float,
    val lengthCm: Float,
    val headCircumferenceCm: Float,
    val note: String
)

data class DevelopmentalMilestone(
    val id: String,
    val childMemberId: String,
    val domain: MilestoneDomain,
    val title: String,
    val achievedDate: String?,
    val isAchieved: Boolean,
    val observation: String,
    val mediaCountLabel: String?
)

data class FeedingLog(
    val id: String,
    val childMemberId: String,
    val feedingType: String,
    val foodDetail: String,
    val timeLabel: String,
    val reactionNote: String
)

data class ImmunizationRecord(
    val id: String,
    val childMemberId: String,
    val vaccineName: String,
    val doseLabel: String,
    val dateLabel: String,
    val provider: String,
    val nextDueLabel: String?,
    val isCompleted: Boolean
)

data class BabyJournalEntry(
    val id: String,
    val childMemberId: String,
    val category: String,
    val timeLabel: String,
    val summary: String,
    val detail: String
)
