package com.example.domain.repository

import com.example.domain.model.ActivityFeedItem
import com.example.domain.model.BabyJournalEntry
import com.example.domain.model.BudgetCategoryItem
import com.example.domain.model.ChildProfile
import com.example.domain.model.DevelopmentalMilestone
import com.example.domain.model.Family
import com.example.domain.model.FamilyAnnouncement
import com.example.domain.model.FamilyDocument
import com.example.domain.model.FamilyEvent
import com.example.domain.model.FamilyMember
import com.example.domain.model.FeedingLog
import com.example.domain.model.FinancialSummary
import com.example.domain.model.GrowthMeasurement
import com.example.domain.model.HealthAppointment
import com.example.domain.model.HealthMeasurement
import com.example.domain.model.ImmunizationRecord
import com.example.domain.model.LabResultPreview
import com.example.domain.model.MedicalProfile
import com.example.domain.model.MedicalRecord
import com.example.domain.model.MedicationItem
import com.example.domain.model.MonthlyComparison
import com.example.domain.model.SavingsGoal
import com.example.domain.model.TransactionItem

/**
 * Clean Architecture boundary for family household data.
 * Phase 1 provides MockFamilyDataSource; Phase 6 will introduce a Room + Retrofit
 * implementation targeting https://api.malakuljibal.com.
 */
interface FamilyDataSource {
    fun getFamily(): Family
    fun getFamilyMembers(): List<FamilyMember>
    fun getAnnouncements(): List<FamilyAnnouncement>
    fun getFinancialSummary(): FinancialSummary
    fun getMonthlyComparisons(): List<MonthlyComparison>
    fun getTransactions(): List<TransactionItem>
    fun getBudgetCategories(): List<BudgetCategoryItem>
    fun getSavingsGoals(): List<SavingsGoal>
    fun getMedicalProfiles(): List<MedicalProfile>
    fun getHealthMeasurements(): List<HealthMeasurement>
    fun getMedicalRecords(): List<MedicalRecord>
    fun getMedications(): List<MedicationItem>
    fun getLabResults(): List<LabResultPreview>
    fun getUpcomingAppointments(): List<HealthAppointment>
    fun getDocuments(): List<FamilyDocument>
    fun getChildProfiles(): List<ChildProfile>
    fun getGrowthMeasurements(): List<GrowthMeasurement>
    fun getMilestones(): List<DevelopmentalMilestone>
    fun getFeedingLogs(): List<FeedingLog>
    fun getImmunizations(): List<ImmunizationRecord>
    fun getBabyJournalEntries(): List<BabyJournalEntry>
    fun getFamilyEvents(): List<FamilyEvent>
    fun getActivityFeed(): List<ActivityFeedItem>
}
