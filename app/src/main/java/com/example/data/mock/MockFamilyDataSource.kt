package com.example.data.mock

import com.example.R
import com.example.domain.model.AccessLevel
import com.example.domain.model.AppModule
import com.example.domain.model.Family
import com.example.domain.model.FamilyAnnouncement
import com.example.domain.model.FamilyMember
import com.example.domain.model.ModulePermission
import com.example.domain.model.RelationshipType
import com.example.domain.model.UserRole
import com.example.domain.repository.FamilyDataSource

/**
 * Sample family dataset for Phase 1 frontend development.
 * All figures, medical entries, and documents are illustrative demo records.
 */
class MockFamilyDataSource : FamilyDataSource {

    companion object {
        const val FAMILY_ID = "fam-mj-001"
        const val ID_TARIQ = "usr-tariq-01"
        const val ID_NADIA = "usr-nadia-02"
        const val ID_AMINA = "usr-amina-03"
        const val ID_ZAYD = "usr-zayd-04"
        const val ID_HAFIZ = "usr-hafiz-05"
    }

    override fun getFamily(): Family = Family(
        id = FAMILY_ID,
        name = "Al-Jibal Household",
        motto = "Our Family. Our Health. Our Legacy.",
        description = "Private multi-generational family workspace uniting household planning, personal health continuity, secure document custody, and early childhood milestones.",
        createdDate = "14 Jan 2024",
        adminMemberId = ID_TARIQ,
        location = "Jakarta, Indonesia"
    )

    override fun getFamilyMembers(): List<FamilyMember> = listOf(
        FamilyMember(
            id = ID_TARIQ,
            familyId = FAMILY_ID,
            fullName = "Tariq Al-Jibal",
            shortName = "Tariq",
            initials = "TA",
            avatarRes = R.drawable.img_avatar_father_1795077628834,
            dateOfBirth = "18 Mar 1990",
            ageLabel = "36 yrs",
            gender = "Male",
            relationship = RelationshipType.HUSBAND,
            role = UserRole.SUPER_ADMIN,
            email = "tariq@malakuljibal.com",
            phone = "+62 812-3401-8890",
            hasIndependentAccount = true,
            bio = "Household administrator overseeing long-term family savings, property archives, and shared planning.",
            financialContributionNote = "Primary household income steward & shared budget approver.",
            monthlyAllowanceOrShare = "Shared Household Lead",
            permissions = listOf(
                ModulePermission(AppModule.COMMAND_CENTER, true, true, "Full household overview"),
                ModulePermission(AppModule.PROFILES, true, true, "Manage family directory & roles"),
                ModulePermission(AppModule.FINANCE, true, true, "Shared + Admin-only accounts"),
                ModulePermission(AppModule.HEALTH, true, true, "Own + authorized child records"),
                ModulePermission(AppModule.DOCUMENTS, true, true, "Vault governance & expiry alerts"),
                ModulePermission(AppModule.CHILD_DEVELOPMENT, true, true, "Co-guardian of Zayd & Amina"),
                ModulePermission(AppModule.CALENDAR, true, true, "All household schedules")
            ),
            personalHighlights = listOf(
                "Annual health checkup completed (Nov 2026)",
                "Passport valid until Aug 2030",
                "Emergency Fund at 78% of household target"
            )
        ),
        FamilyMember(
            id = ID_NADIA,
            familyId = FAMILY_ID,
            fullName = "Nadia Al-Jibal",
            shortName = "Nadia",
            initials = "NA",
            avatarRes = R.drawable.img_avatar_mother_1795077661681,
            dateOfBirth = "09 Sep 1993",
            ageLabel = "33 yrs",
            gender = "Female",
            relationship = RelationshipType.WIFE,
            role = UserRole.PARENT,
            email = "nadia@malakuljibal.com",
            phone = "+62 811-9045-2217",
            hasIndependentAccount = true,
            bio = "Co-head of household coordinating family nutrition, pediatric growth tracking, and school milestones.",
            financialContributionNote = "Manages household groceries, pediatric care, and education savings.",
            monthlyAllowanceOrShare = "Shared Parent Budget",
            permissions = listOf(
                ModulePermission(AppModule.COMMAND_CENTER, true, true, "Shared household overview"),
                ModulePermission(AppModule.PROFILES, true, true, "Own & child profiles"),
                ModulePermission(AppModule.FINANCE, true, true, "Shared & assigned expenses"),
                ModulePermission(AppModule.HEALTH, true, true, "Own + authorized children"),
                ModulePermission(AppModule.DOCUMENTS, true, true, "Shared, own & child vault"),
                ModulePermission(AppModule.CHILD_DEVELOPMENT, true, true, "Primary pediatric logger"),
                ModulePermission(AppModule.CALENDAR, true, true, "Family & pediatric events")
            ),
            personalHighlights = listOf(
                "Logged Zayd's 14-month growth measurements",
                "Driver's license renewal reminder in 38 days",
                "Dental checkup scheduled for 28 Nov"
            )
        ),
        FamilyMember(
            id = ID_AMINA,
            familyId = FAMILY_ID,
            fullName = "Amina Al-Jibal",
            shortName = "Amina",
            initials = "AA",
            avatarRes = null,
            dateOfBirth = "22 May 2019",
            ageLabel = "7 yrs",
            gender = "Female",
            relationship = RelationshipType.DAUGHTER,
            role = UserRole.CHILD_PROFILE,
            email = null,
            phone = null,
            hasIndependentAccount = false,
            managedByParentIds = listOf(ID_TARIQ, ID_NADIA),
            bio = "Grade 2 student passionate about watercolor sketching, swimming, and reading.",
            financialContributionNote = "Covered under Child Education & Tuition savings fund.",
            monthlyAllowanceOrShare = "Education Fund Beneficiary",
            permissions = listOf(
                ModulePermission(AppModule.PROFILES, true, false, "Managed by Tariq & Nadia"),
                ModulePermission(AppModule.HEALTH, true, false, "Parents authorized"),
                ModulePermission(AppModule.DOCUMENTS, true, false, "Birth cert, school & passport"),
                ModulePermission(AppModule.CHILD_DEVELOPMENT, true, false, "Growth & school milestones")
            ),
            personalHighlights = listOf(
                "Completed Grade 2 first-semester report card",
                "Annual pediatric vision screening normal",
                "Passport expires in 54 days — renewal queued"
            )
        ),
        FamilyMember(
            id = ID_ZAYD,
            familyId = FAMILY_ID,
            fullName = "Zayd Al-Jibal",
            shortName = "Zayd",
            initials = "ZA",
            avatarRes = R.drawable.img_avatar_child_1795077630915,
            dateOfBirth = "12 Sep 2025",
            ageLabel = "14 mos",
            gender = "Male",
            relationship = RelationshipType.SON,
            role = UserRole.CHILD_PROFILE,
            email = null,
            phone = null,
            hasIndependentAccount = false,
            managedByParentIds = listOf(ID_TARIQ, ID_NADIA),
            bio = "Curious 14-month-old toddler taking steady independent steps and exploring family meals.",
            financialContributionNote = "Tracked under Pediatric Care & Early Education reserve.",
            monthlyAllowanceOrShare = "Managed Child Profile",
            permissions = listOf(
                ModulePermission(AppModule.PROFILES, true, false, "Managed by Tariq & Nadia"),
                ModulePermission(AppModule.HEALTH, true, false, "Parents authorized"),
                ModulePermission(AppModule.CHILD_DEVELOPMENT, true, false, "Full pediatric tracker active")
            ),
            personalHighlights = listOf(
                "Latest weight: 10.1 kg · Height: 78.5 cm",
                "Walked 6 independent steps on 08 Nov",
                "MMR Dose 1 scheduled for 05 Dec 2026"
            )
        ),
        FamilyMember(
            id = ID_HAFIZ,
            familyId = FAMILY_ID,
            fullName = "Hafiz Al-Jibal",
            shortName = "Hafiz",
            initials = "HA",
            avatarRes = null,
            dateOfBirth = "03 Feb 1962",
            ageLabel = "64 yrs",
            gender = "Male",
            relationship = RelationshipType.FATHER,
            role = UserRole.FAMILY_MEMBER,
            email = "hafiz@malakuljibal.com",
            phone = "+62 816-7712-4500",
            hasIndependentAccount = true,
            bio = "Retired civil engineer and family elder enjoying morning walks, gardening, and weekend visits with grandchildren.",
            financialContributionNote = "Independent retirement finances; participates in shared family gatherings.",
            monthlyAllowanceOrShare = "Private Personal Finances",
            permissions = listOf(
                ModulePermission(AppModule.COMMAND_CENTER, true, false, "Shared family updates only"),
                ModulePermission(AppModule.PROFILES, true, true, "Own personal profile"),
                ModulePermission(AppModule.FINANCE, false, false, "Private — household finances restricted"),
                ModulePermission(AppModule.HEALTH, true, true, "Own private medical profile"),
                ModulePermission(AppModule.DOCUMENTS, true, false, "Shared & own documents only"),
                ModulePermission(AppModule.CALENDAR, true, false, "Shared & personal events")
            ),
            personalHighlights = listOf(
                "Morning BP log: 124/78 mmHg (steady)",
                "Cardiology follow-up on 02 Dec 2026",
                "Medical records set to Private (self-only)"
            )
        )
    )

    override fun getAnnouncements(): List<FamilyAnnouncement> = listOf(
        FamilyAnnouncement(
            id = "ann-1",
            title = "December Family Retreat & Passport Check",
            message = "Please review Amina's passport renewal timeline before confirming our Bandung year-end family gathering.",
            postedBy = "Tariq Al-Jibal",
            dateLabel = "18 Nov 2026"
        ),
        FamilyAnnouncement(
            id = "ann-2",
            title = "Zayd's 15-Month Pediatric Checkup Confirmed",
            message = "Booked with Dr. Rahma Putri at Bunda Family Clinic for Saturday, 05 Dec at 09:30.",
            postedBy = "Nadia Al-Jibal",
            dateLabel = "16 Nov 2026"
        )
    )

    override fun getFinancialSummary() = MockModuleSamples.financialSummary
    override fun getMonthlyComparisons() = MockModuleSamples.monthlyComparisons
    override fun getTransactions() = MockModuleSamples.transactions
    override fun getBudgetCategories() = MockModuleSamples.budgetCategories
    override fun getSavingsGoals() = MockModuleSamples.savingsGoals
    override fun getMedicalProfiles() = MockModuleSamples.medicalProfiles
    override fun getHealthMeasurements() = MockModuleSamples.healthMeasurements
    override fun getMedicalRecords() = MockModuleSamples.medicalRecords
    override fun getMedications() = MockModuleSamples.medications
    override fun getLabResults() = MockModuleSamples.labResults
    override fun getUpcomingAppointments() = MockModuleSamples.upcomingAppointments
    override fun getDocuments() = MockModuleSamples.documents
    override fun getChildProfiles() = MockModuleSamples.childProfiles
    override fun getGrowthMeasurements() = MockModuleSamples.growthMeasurements
    override fun getMilestones() = MockModuleSamples.milestones
    override fun getFeedingLogs() = MockModuleSamples.feedingLogs
    override fun getImmunizations() = MockModuleSamples.immunizations
    override fun getBabyJournalEntries() = MockModuleSamples.babyJournalEntries
    override fun getFamilyEvents() = MockModuleSamples.familyEvents
    override fun getActivityFeed() = MockModuleSamples.activityFeed
}
