package com.example.data.mock

import com.example.data.mock.MockFamilyDataSource.Companion.ID_AMINA
import com.example.data.mock.MockFamilyDataSource.Companion.ID_HAFIZ
import com.example.data.mock.MockFamilyDataSource.Companion.ID_NADIA
import com.example.data.mock.MockFamilyDataSource.Companion.ID_TARIQ
import com.example.data.mock.MockFamilyDataSource.Companion.ID_ZAYD
import com.example.domain.model.*

/**
 * Structured Phase 1 mock datasets for Finance, Health, Documents, Child Development,
 * Family Calendar, and Chronological Activity Feed.
 */
object MockModuleSamples {

    val financialSummary = FinancialSummary(
        monthLabel = "November 2026",
        currencySymbol = "$",
        monthlyIncome = 9450,
        monthlyExpenses = 5620,
        availableCash = 14820,
        totalSavings = 68400,
        monthlyBudgetLimit = 6400
    )

    val monthlyComparisons = listOf(
        MonthlyComparison("Jun", 8900, 5400),
        MonthlyComparison("Jul", 9100, 5850),
        MonthlyComparison("Aug", 9200, 5210),
        MonthlyComparison("Sep", 9050, 6100),
        MonthlyComparison("Oct", 9300, 5480),
        MonthlyComparison("Nov", 9450, 5620)
    )

    val transactions = listOf(
        TransactionItem(
            id = "tx-01",
            title = "Monthly Architecture Consulting",
            category = "Primary Salary",
            amount = 7200,
            type = TransactionType.INCOME,
            date = "01 Nov 2026",
            paymentMethod = "Bank Transfer · Family Checking",
            responsibleMemberId = ID_TARIQ,
            responsibleMemberName = "Tariq Al-Jibal",
            description = "Monthly household salary deposit.",
            hasReceipt = true,
            accessLevel = AccessLevel.FAMILY_SHARED
        ),
        TransactionItem(
            id = "tx-02",
            title = "Design Advisory Retainer",
            category = "Secondary Income",
            amount = 2250,
            type = TransactionType.INCOME,
            date = "05 Nov 2026",
            paymentMethod = "Bank Transfer · Joint Account",
            responsibleMemberId = ID_NADIA,
            responsibleMemberName = "Nadia Al-Jibal",
            description = "Part-time studio creative direction retainer.",
            hasReceipt = true,
            accessLevel = AccessLevel.FAMILY_SHARED
        ),
        TransactionItem(
            id = "tx-03",
            title = "Organic Groceries & Fresh Market",
            category = "Groceries & Kitchen",
            amount = 640,
            type = TransactionType.EXPENSE,
            date = "16 Nov 2026",
            paymentMethod = "Family Debit Card",
            responsibleMemberId = ID_NADIA,
            responsibleMemberName = "Nadia Al-Jibal",
            description = "Bi-weekly produce, dairy, and MPASI ingredients for Zayd.",
            hasReceipt = true,
            accessLevel = AccessLevel.FAMILY_SHARED
        ),
        TransactionItem(
            id = "tx-04",
            title = "Amina Elementary Tuition & Art Club",
            category = "Child Education",
            amount = 980,
            type = TransactionType.EXPENSE,
            date = "10 Nov 2026",
            paymentMethod = "Auto-Debit · Education Account",
            responsibleMemberId = ID_TARIQ,
            responsibleMemberName = "Tariq Al-Jibal",
            description = "Monthly tuition and extracurricular watercolor workshop.",
            hasReceipt = true,
            accessLevel = AccessLevel.FAMILY_SHARED
        ),
        TransactionItem(
            id = "tx-05",
            title = "Bunda Family Clinic · Pediatric Check",
            category = "Healthcare & Wellness",
            amount = 215,
            type = TransactionType.EXPENSE,
            date = "12 Nov 2026",
            paymentMethod = "Health Savings Card",
            responsibleMemberId = ID_NADIA,
            responsibleMemberName = "Nadia Al-Jibal",
            description = "14-month growth consultation and vitamin supplement.",
            hasReceipt = true,
            accessLevel = AccessLevel.PARENT_MANAGED
        ),
        TransactionItem(
            id = "tx-06",
            title = "Property Tax & Maintenance Reserve",
            category = "Housing & Utilities",
            amount = 1850,
            type = TransactionType.EXPENSE,
            date = "04 Nov 2026",
            paymentMethod = "Reserve Transfer",
            responsibleMemberId = ID_TARIQ,
            responsibleMemberName = "Tariq Al-Jibal",
            description = "Scheduled estate reserve allocation (Admin-only record).",
            hasReceipt = true,
            accessLevel = AccessLevel.ADMIN_ONLY
        ),
        TransactionItem(
            id = "tx-07",
            title = "Personal Anniversary Gift Reserve",
            category = "Personal",
            amount = 320,
            type = TransactionType.EXPENSE,
            date = "14 Nov 2026",
            paymentMethod = "Personal Card",
            responsibleMemberId = ID_TARIQ,
            responsibleMemberName = "Tariq Al-Jibal",
            description = "Private personal expense visible only to Tariq.",
            hasReceipt = false,
            accessLevel = AccessLevel.PRIVATE_PERSONAL
        )
    )

    val budgetCategories = listOf(
        BudgetCategoryItem("b-1", "Housing & Utilities", 2100, 1980),
        BudgetCategoryItem("b-2", "Groceries & Kitchen", 1200, 1085),
        BudgetCategoryItem("b-3", "Child Education", 1000, 980),
        BudgetCategoryItem("b-4", "Healthcare & Wellness", 600, 695),
        BudgetCategoryItem("b-5", "Transport & Fuel", 500, 410),
        BudgetCategoryItem("b-6", "Family Leisure & Charity", 1000, 470)
    )

    val savingsGoals = listOf(
        SavingsGoal("sg-1", "Emergency Fund", "12 months of core household security", 30000, 23400, "2027", AccessLevel.FAMILY_SHARED),
        SavingsGoal("sg-2", "Children's Higher Education", "Dedicated long-term trust for Amina & Zayd", 40000, 24800, "2036", AccessLevel.PARENT_MANAGED),
        SavingsGoal("sg-3", "Family Sanctuary Renovation", "Courtyard library & solar battery upgrade", 18000, 12900, "2027", AccessLevel.FAMILY_SHARED),
        SavingsGoal("sg-4", "Year-End Heritage Retreat", "Annual multi-generational family getaway", 8500, 7300, "2026", AccessLevel.FAMILY_SHARED)
    )

    val medicalProfiles = listOf(
        MedicalProfile(
            memberId = ID_TARIQ,
            bloodType = "O+",
            allergies = listOf("None reported"),
            conditions = listOf("Mild seasonal allergic rhinitis"),
            relevantHistory = "Annual preventive checkup Nov 2026; lipid panel within laboratory range.",
            emergencyContactName = "Nadia Al-Jibal (Wife)",
            emergencyContactPhone = "+62 811-9045-2217",
            primaryPhysician = "Dr. Hendra Kusuma, Sp.PD",
            accessLevel = AccessLevel.PRIVATE_PERSONAL,
            authorizedViewerIds = listOf(ID_NADIA)
        ),
        MedicalProfile(
            memberId = ID_NADIA,
            bloodType = "A+",
            allergies = listOf("Penicillin (mild rash)"),
            conditions = listOf("None active"),
            relevantHistory = "Postpartum follow-up completed; iron levels restored to lab reference range.",
            emergencyContactName = "Tariq Al-Jibal (Husband)",
            emergencyContactPhone = "+62 812-3401-8890",
            primaryPhysician = "Dr. Sinta Dewi, Sp.OG",
            accessLevel = AccessLevel.PRIVATE_PERSONAL,
            authorizedViewerIds = listOf(ID_TARIQ)
        ),
        MedicalProfile(
            memberId = ID_AMINA,
            bloodType = "O+",
            allergies = listOf("Dust mites"),
            conditions = listOf("None active"),
            relevantHistory = "Routine school pediatric checkups; full primary immunization series completed.",
            emergencyContactName = "Nadia Al-Jibal (Mother)",
            emergencyContactPhone = "+62 811-9045-2217",
            primaryPhysician = "Dr. Rahma Putri, Sp.A",
            accessLevel = AccessLevel.PARENT_MANAGED,
            authorizedViewerIds = listOf(ID_TARIQ, ID_NADIA)
        ),
        MedicalProfile(
            memberId = ID_ZAYD,
            bloodType = "A+",
            allergies = listOf("No known food allergies (egg & peanut introduced)"),
            conditions = listOf("Healthy toddler — routine growth monitoring"),
            relevantHistory = "Full-term delivery (39w2d); breastfeeding + balanced MPASI family food.",
            emergencyContactName = "Nadia Al-Jibal (Mother)",
            emergencyContactPhone = "+62 811-9045-2217",
            primaryPhysician = "Dr. Rahma Putri, Sp.A",
            accessLevel = AccessLevel.PARENT_MANAGED,
            authorizedViewerIds = listOf(ID_TARIQ, ID_NADIA)
        ),
        MedicalProfile(
            memberId = ID_HAFIZ,
            bloodType = "O+",
            allergies = listOf("Sulfonamides"),
            conditions = listOf("Essential hypertension (managed)"),
            relevantHistory = "Monitored quarterly with cardiologist; home blood pressure log kept privately.",
            emergencyContactName = "Tariq Al-Jibal (Son)",
            emergencyContactPhone = "+62 812-3401-8890",
            primaryPhysician = "Dr. Arman Wibowo, Sp.JP",
            accessLevel = AccessLevel.PRIVATE_PERSONAL,
            authorizedViewerIds = emptyList() // Strictly private to Hafiz unless he grants access!
        )
    )

    val healthMeasurements = listOf(
        HealthMeasurement("hm-1", ID_TARIQ, MeasurementType.WEIGHT, "75.4 kg", 75.4f, "02 Oct", "Morning weigh-in"),
        HealthMeasurement("hm-2", ID_TARIQ, MeasurementType.WEIGHT, "75.1 kg", 75.1f, "18 Oct", "Steady after jogging"),
        HealthMeasurement("hm-3", ID_TARIQ, MeasurementType.WEIGHT, "74.8 kg", 74.8f, "04 Nov", "Morning weigh-in"),
        HealthMeasurement("hm-4", ID_TARIQ, MeasurementType.WEIGHT, "74.6 kg", 74.6f, "17 Nov", "Current baseline"),
        HealthMeasurement("hm-5", ID_TARIQ, MeasurementType.BLOOD_PRESSURE, "118/76 mmHg", 118f, "17 Nov", "Resting morning reading"),
        HealthMeasurement("hm-6", ID_TARIQ, MeasurementType.BLOOD_GLUCOSE, "91 mg/dL", 91f, "05 Nov", "Fasting lab draw"),

        HealthMeasurement("hm-7", ID_NADIA, MeasurementType.WEIGHT, "56.2 kg", 56.2f, "01 Oct", "Routine log"),
        HealthMeasurement("hm-8", ID_NADIA, MeasurementType.WEIGHT, "55.9 kg", 55.9f, "20 Oct", "Routine log"),
        HealthMeasurement("hm-9", ID_NADIA, MeasurementType.WEIGHT, "55.7 kg", 55.7f, "14 Nov", "Current reading"),
        HealthMeasurement("hm-10", ID_NADIA, MeasurementType.BLOOD_PRESSURE, "112/72 mmHg", 112f, "14 Nov", "Normal clinic check"),

        HealthMeasurement("hm-11", ID_HAFIZ, MeasurementType.BLOOD_PRESSURE, "128/82 mmHg", 128f, "28 Oct", "Morning reading"),
        HealthMeasurement("hm-12", ID_HAFIZ, MeasurementType.BLOOD_PRESSURE, "126/80 mmHg", 126f, "05 Nov", "After morning walk"),
        HealthMeasurement("hm-13", ID_HAFIZ, MeasurementType.BLOOD_PRESSURE, "124/78 mmHg", 124f, "15 Nov", "Well controlled"),
        HealthMeasurement("hm-14", ID_HAFIZ, MeasurementType.BLOOD_GLUCOSE, "102 mg/dL", 102f, "12 Nov", "Fasting check")
    )

    val medicalRecords = listOf(
        MedicalRecord(
            id = "mr-1",
            memberId = ID_TARIQ,
            consultationDate = "05 Nov 2026",
            facility = "Medistra Family Medical Center",
            doctorName = "Dr. Hendra Kusuma, Sp.PD",
            reportedSymptoms = "Routine annual executive preventive checkup; no acute complaints.",
            recordedDiagnosis = "Good general health; mild seasonal rhinitis",
            prescribedSummary = "Vitamin D3 1000 IU daily; saline nasal spray as needed",
            doctorRecommendations = "Maintain 150 min/week aerobic activity; repeat routine labs in 12 months.",
            accessLevel = AccessLevel.PRIVATE_PERSONAL
        ),
        MedicalRecord(
            id = "mr-2",
            memberId = ID_ZAYD,
            consultationDate = "12 Nov 2026",
            facility = "Bunda Family Clinic",
            doctorName = "Dr. Rahma Putri, Sp.A",
            reportedSymptoms = "Routine 14-month well-child checkup & milestone review.",
            recordedDiagnosis = "Healthy toddler growth progression",
            prescribedSummary = "Continue iron-rich family foods; vitamin D drops as advised",
            doctorRecommendations = "Schedule MMR Dose 1 and Varicella vaccine at 15-month visit.",
            accessLevel = AccessLevel.PARENT_MANAGED
        ),
        MedicalRecord(
            id = "mr-3",
            memberId = ID_NADIA,
            consultationDate = "19 Oct 2026",
            facility = "Premier Women's Wellness",
            doctorName = "Dr. Sinta Dewi, Sp.OG",
            reportedSymptoms = "Routine annual wellness exam & ferritin recheck.",
            recordedDiagnosis = "Normal wellness evaluation",
            prescribedSummary = "Balanced dietary iron; routine prenatal-postpartum multivitamin",
            doctorRecommendations = "Follow-up in 12 months.",
            accessLevel = AccessLevel.PRIVATE_PERSONAL
        ),
        MedicalRecord(
            id = "mr-4",
            memberId = ID_HAFIZ,
            consultationDate = "08 Oct 2026",
            facility = "Harapan Heart Outpatient Clinic",
            doctorName = "Dr. Arman Wibowo, Sp.JP",
            reportedSymptoms = "Quarterly blood pressure review; no chest discomfort or dizziness.",
            recordedDiagnosis = "Stable managed essential hypertension",
            prescribedSummary = "Continue prescribed antihypertensive regimen without change",
            doctorRecommendations = "Continue daily morning walk and low-sodium diet; review in Dec 2026.",
            accessLevel = AccessLevel.PRIVATE_PERSONAL
        )
    )

    val medications = listOf(
        MedicationItem("med-1", ID_TARIQ, "Vitamin D3", "1000 IU", "Once daily with breakfast", "05 Nov 2026", null, "Dr. Hendra Kusuma", "Preventive supplementation", true),
        MedicationItem("med-2", ID_ZAYD, "Pediatric Vitamin D Drops", "400 IU", "Once daily", "12 Sep 2025", null, "Dr. Rahma Putri", "Administer in morning", true),
        MedicationItem("med-3", ID_HAFIZ, "Amlodipine", "5 mg", "Once daily (morning)", "10 Jan 2023", null, "Dr. Arman Wibowo", "Take at consistent time daily", true),
        MedicationItem("med-4", ID_NADIA, "Iron + Folate Supplement", "60 mg", "Once daily", "15 Jul 2026", "15 Oct 2026", "Dr. Sinta Dewi", "Course completed", false)
    )

    val labResults = listOf(
        LabResultPreview("lab-1", ID_TARIQ, "Fasting Blood Glucose", "Blood Chemistry", "05 Nov 2026", "Prodia Clinical Lab", "91 mg/dL", "Lab Ref: 70 – 99 mg/dL", "PDF Report (Mock)"),
        LabResultPreview("lab-2", ID_TARIQ, "Total Cholesterol", "Lipid Panel", "05 Nov 2026", "Prodia Clinical Lab", "184 mg/dL", "Lab Ref: < 200 mg/dL", "PDF Report (Mock)"),
        LabResultPreview("lab-3", ID_NADIA, "Serum Ferritin", "Hematology", "19 Oct 2026", "Prodia Clinical Lab", "48 ng/mL", "Lab Ref: 15 – 150 ng/mL", "PDF Report (Mock)"),
        LabResultPreview("lab-4", ID_HAFIZ, "HbA1c", "Metabolic Panel", "08 Oct 2026", "Harapan Diagnostics", "5.6 %", "Lab Ref: < 5.7 %", "PDF Report (Mock)")
    )

    val upcomingAppointments = listOf(
        HealthAppointment("apt-1", ID_NADIA, "Nadia Al-Jibal", "Semi-Annual Dental Checkup", "Menteng Dental Studio", "Drg. Clarissa Tan", "28 Nov 2026 · 10:30", AccessLevel.FAMILY_SHARED),
        HealthAppointment("apt-2", ID_HAFIZ, "Hafiz Al-Jibal", "Quarterly Cardiology Follow-up", "Harapan Heart Outpatient", "Dr. Arman Wibowo, Sp.JP", "02 Dec 2026 · 09:00", AccessLevel.PRIVATE_PERSONAL),
        HealthAppointment("apt-3", ID_ZAYD, "Zayd Al-Jibal", "15-Month Growth & MMR Dose 1", "Bunda Family Clinic", "Dr. Rahma Putri, Sp.A", "05 Dec 2026 · 09:30", AccessLevel.PARENT_MANAGED)
    )

    val documents = listOf(
        FamilyDocument(
            id = "doc-01",
            title = "Family Card (Kartu Keluarga)",
            category = DocumentCategory.IDENTITY,
            ownerMemberId = ID_TARIQ,
            ownerName = "Al-Jibal Household",
            issueDate = "14 Oct 2025",
            expirationDate = null,
            daysUntilExpiry = null,
            description = "Official household registration updated after Zayd's birth.",
            tags = listOf("Official", "Household", "Verified"),
            uploadedBy = "Tariq Al-Jibal",
            uploadTimestamp = "18 Oct 2025 · 14:22",
            fileFormat = "PDF",
            fileSizeLabel = "1.8 MB",
            versionLabel = "v2.0",
            status = DocumentStatus.ACTIVE,
            accessLevel = AccessLevel.FAMILY_SHARED,
            recentAuditEntries = listOf("Viewed by Nadia · 11 Nov 2026", "Updated to v2.0 by Tariq · 18 Oct 2025")
        ),
        FamilyDocument(
            id = "doc-02",
            title = "Amina Al-Jibal · Passport",
            category = DocumentCategory.PASSPORT,
            ownerMemberId = ID_AMINA,
            ownerName = "Amina Al-Jibal",
            issueDate = "12 Jan 2022",
            expirationDate = "12 Jan 2027",
            daysUntilExpiry = 54,
            description = "Child passport — requires renewal before international travel (6-month validity rule).",
            tags = listOf("Travel", "Renewal Soon", "Child"),
            uploadedBy = "Nadia Al-Jibal",
            uploadTimestamp = "20 Jan 2022 · 09:15",
            fileFormat = "PDF",
            fileSizeLabel = "2.4 MB",
            versionLabel = "v1.0",
            status = DocumentStatus.EXPIRING_SOON,
            accessLevel = AccessLevel.PARENT_MANAGED,
            recentAuditEntries = listOf("Expiry reminder triggered · 18 Nov 2026", "Viewed by Tariq · 14 Nov 2026")
        ),
        FamilyDocument(
            id = "doc-03",
            title = "Nadia Al-Jibal · Driver's License",
            category = DocumentCategory.VEHICLE,
            ownerMemberId = ID_NADIA,
            ownerName = "Nadia Al-Jibal",
            issueDate = "27 Dec 2021",
            expirationDate = "27 Dec 2026",
            daysUntilExpiry = 38,
            description = "SIM A renewal window opens 30 days prior to expiration.",
            tags = listOf("Vehicle", "Expiring"),
            uploadedBy = "Nadia Al-Jibal",
            uploadTimestamp = "02 Jan 2022 · 11:04",
            fileFormat = "JPG",
            fileSizeLabel = "940 KB",
            versionLabel = "v1.0",
            status = DocumentStatus.EXPIRING_SOON,
            accessLevel = AccessLevel.PRIVATE_PERSONAL,
            authorizedViewerIds = listOf(ID_TARIQ),
            recentAuditEntries = listOf("Renewal alert created · 17 Nov 2026")
        ),
        FamilyDocument(
            id = "doc-04",
            title = "Marriage Certificate (Akta Nikah)",
            category = DocumentCategory.MARRIAGE_CERT,
            ownerMemberId = ID_TARIQ,
            ownerName = "Tariq & Nadia",
            issueDate = "11 Aug 2017",
            expirationDate = null,
            daysUntilExpiry = null,
            description = "Certified digital scan of marriage registry booklet.",
            tags = listOf("Civil Registry", "Permanent"),
            uploadedBy = "Tariq Al-Jibal",
            uploadTimestamp = "15 Jan 2024 · 20:10",
            fileFormat = "PDF",
            fileSizeLabel = "3.1 MB",
            versionLabel = "v1.0",
            status = DocumentStatus.ACTIVE,
            accessLevel = AccessLevel.PARENT_MANAGED,
            recentAuditEntries = listOf("Verified integrity checksum · 01 Nov 2026")
        ),
        FamilyDocument(
            id = "doc-05",
            title = "Zayd Al-Jibal · Birth Certificate",
            category = DocumentCategory.BIRTH_CERT,
            ownerMemberId = ID_ZAYD,
            ownerName = "Zayd Al-Jibal",
            issueDate = "25 Sep 2025",
            expirationDate = null,
            daysUntilExpiry = null,
            description = "Official civil birth certificate and hospital birth summary.",
            tags = listOf("Birth", "Pediatric", "Permanent"),
            uploadedBy = "Tariq Al-Jibal",
            uploadTimestamp = "26 Sep 2025 · 16:40",
            fileFormat = "PDF",
            fileSizeLabel = "1.4 MB",
            versionLabel = "v1.0",
            status = DocumentStatus.ACTIVE,
            accessLevel = AccessLevel.PARENT_MANAGED,
            recentAuditEntries = listOf("Uploaded by Tariq · 26 Sep 2025")
        ),
        FamilyDocument(
            id = "doc-06",
            title = "Family Health & Life Insurance Policy",
            category = DocumentCategory.INSURANCE,
            ownerMemberId = ID_TARIQ,
            ownerName = "Al-Jibal Household",
            issueDate = "01 Jan 2026",
            expirationDate = "31 Dec 2026",
            daysUntilExpiry = 42,
            description = "Comprehensive inpatient and outpatient family protection policy #MJ-88412.",
            tags = listOf("Insurance", "Annual Renewal"),
            uploadedBy = "Tariq Al-Jibal",
            uploadTimestamp = "04 Jan 2026 · 08:30",
            fileFormat = "PDF",
            fileSizeLabel = "4.6 MB",
            versionLabel = "v3.1",
            status = DocumentStatus.EXPIRING_SOON,
            accessLevel = AccessLevel.FAMILY_SHARED,
            recentAuditEntries = listOf("Viewed by Nadia · 12 Nov 2026")
        ),
        FamilyDocument(
            id = "doc-07",
            title = "Residence Title Deed (SHM #04912)",
            category = DocumentCategory.PROPERTY,
            ownerMemberId = ID_TARIQ,
            ownerName = "Tariq Al-Jibal",
            issueDate = "06 Jun 2021",
            expirationDate = null,
            daysUntilExpiry = null,
            description = "Primary residence ownership certificate stored with admin-only access.",
            tags = listOf("Property", "Legacy Asset", "Restricted"),
            uploadedBy = "Tariq Al-Jibal",
            uploadTimestamp = "15 Jan 2024 · 20:45",
            fileFormat = "PDF",
            fileSizeLabel = "6.2 MB",
            versionLabel = "v1.0",
            status = DocumentStatus.ACTIVE,
            accessLevel = AccessLevel.ADMIN_ONLY,
            recentAuditEntries = listOf("Accessed by Tariq · 02 Nov 2026")
        ),
        FamilyDocument(
            id = "doc-08",
            title = "Hafiz Al-Jibal · Cardiology Summary",
            category = DocumentCategory.MEDICAL,
            ownerMemberId = ID_HAFIZ,
            ownerName = "Hafiz Al-Jibal",
            issueDate = "08 Oct 2026",
            expirationDate = null,
            daysUntilExpiry = null,
            description = "Private outpatient consultation summary and ECG report.",
            tags = listOf("Medical", "Private"),
            uploadedBy = "Hafiz Al-Jibal",
            uploadTimestamp = "09 Oct 2026 · 10:12",
            fileFormat = "PDF",
            fileSizeLabel = "1.2 MB",
            versionLabel = "v1.0",
            status = DocumentStatus.ACTIVE,
            accessLevel = AccessLevel.PRIVATE_PERSONAL,
            recentAuditEntries = listOf("Uploaded by Hafiz · 09 Oct 2026")
        )
    )

    val childProfiles = listOf(
        ChildProfile(
            memberId = ID_ZAYD,
            fullName = "Zayd Al-Jibal",
            dateOfBirth = "12 Sep 2025",
            ageSummary = "14 months, 7 days",
            gender = "Male",
            birthWeightKg = 3.35f,
            birthLengthCm = 50.0f,
            birthHeadCircumferenceCm = 34.5f,
            birthHistoryNote = "Spontaneous full-term delivery (39w2d) at Bunda Hospital. Immediate skin-to-skin contact and exclusive breastfeeding for 6 months.",
            latestWeightKg = 10.1f,
            latestLengthCm = 78.5f,
            latestHeadCircumferenceCm = 46.8f,
            lastMeasuredDate = "12 Nov 2026",
            nextImmunizationSummary = "MMR Dose 1 · Scheduled 05 Dec 2026"
        ),
        ChildProfile(
            memberId = ID_AMINA,
            fullName = "Amina Al-Jibal",
            dateOfBirth = "22 May 2019",
            ageSummary = "7 years, 5 months",
            gender = "Female",
            birthWeightKg = 3.18f,
            birthLengthCm = 49.0f,
            birthHeadCircumferenceCm = 33.8f,
            birthHistoryNote = "Full-term delivery (39w5d). Healthy childhood growth and active school athletics.",
            latestWeightKg = 23.4f,
            latestLengthCm = 123.5f,
            latestHeadCircumferenceCm = 51.4f,
            lastMeasuredDate = "28 Oct 2026",
            nextImmunizationSummary = "Annual Influenza Booster · May 2027"
        )
    )

    val growthMeasurements = listOf(
        GrowthMeasurement("gm-1", ID_ZAYD, 0, "Sep 25", 3.35f, 50.0f, 34.5f, "Birth measurement"),
        GrowthMeasurement("gm-2", ID_ZAYD, 3, "Dec 25", 6.10f, 61.2f, 40.4f, "3-month pediatric check"),
        GrowthMeasurement("gm-3", ID_ZAYD, 6, "Mar 26", 7.80f, 67.8f, 43.2f, "Started MPASI solids"),
        GrowthMeasurement("gm-4", ID_ZAYD, 9, "Jun 26", 8.85f, 72.0f, 45.0f, "Crawling & pulling to stand"),
        GrowthMeasurement("gm-5", ID_ZAYD, 12, "Sep 26", 9.60f, 76.1f, 46.2f, "First birthday well-child check"),
        GrowthMeasurement("gm-6", ID_ZAYD, 14, "Nov 26", 10.10f, 78.5f, 46.8f, "Walking independently"),

        GrowthMeasurement("gm-7", ID_AMINA, 60, "May 24", 18.6f, 110.2f, 50.4f, "Age 5 annual checkup"),
        GrowthMeasurement("gm-8", ID_AMINA, 72, "May 25", 20.9f, 116.8f, 50.9f, "Age 6 school entry physical"),
        GrowthMeasurement("gm-9", ID_AMINA, 84, "May 26", 22.5f, 121.0f, 51.2f, "Age 7 checkup"),
        GrowthMeasurement("gm-10", ID_AMINA, 89, "Oct 26", 23.4f, 123.5f, 51.4f, "Autumn school height & weight")
    )

    val milestones = listOf(
        DevelopmentalMilestone("ms-1", ID_ZAYD, MilestoneDomain.GROSS_MOTOR, "Walks several independent steps", "08 Nov 2026", true, "Walked 6 steady steps from sofa to Tariq without holding on.", "2 photos · 1 video"),
        DevelopmentalMilestone("ms-2", ID_ZAYD, MilestoneDomain.FINE_MOTOR, "Stacks two wooden blocks", "29 Oct 2026", true, "Carefully stacks 2–3 soft maple blocks during morning play.", "1 photo"),
        DevelopmentalMilestone("ms-3", ID_ZAYD, MilestoneDomain.LANGUAGE, "Says 4+ meaningful words", "14 Nov 2026", true, "Consistently says 'Mama', 'Baba', 'Air', and 'Amina'.", null),
        DevelopmentalMilestone("ms-4", ID_ZAYD, MilestoneDomain.COGNITIVE, "Points to familiar picture book objects", "11 Nov 2026", true, "Points to moon, cat, and car when asked.", null),
        DevelopmentalMilestone("ms-5", ID_ZAYD, MilestoneDomain.SOCIAL_EMOTIONAL, "Waves goodbye & shares toys", "03 Nov 2026", true, "Smiles warmly and hands ball back to Amina.", "1 photo"),
        DevelopmentalMilestone("ms-6", ID_ZAYD, MilestoneDomain.FINE_MOTOR, "Uses spoon with minimal spilling", null, false, "Actively practicing scooping soft oatmeal at breakfast.", null),

        DevelopmentalMilestone("ms-7", ID_AMINA, MilestoneDomain.COGNITIVE, "Reads chapter books independently", "10 Oct 2026", true, "Finished her first 80-page illustrated storybook.", null),
        DevelopmentalMilestone("ms-8", ID_AMINA, MilestoneDomain.GROSS_MOTOR, "Swims 15m freestyle confidently", "25 Oct 2026", true, "Completed level-2 swim assessment.", "1 photo")
    )

    val feedingLogs = listOf(
        FeedingLog("fl-1", ID_ZAYD, "MPASI Breakfast", "Warm oat porridge, mashed banana, chia & egg yolk", "07:30 · Today", "Ate full bowl enthusiastically · No reaction"),
        FeedingLog("fl-2", ID_ZAYD, "Breastfeeding", "Morning nursing session (14 mins)", "10:15 · Today", "Calm and settled before morning nap"),
        FeedingLog("fl-3", ID_ZAYD, "MPASI Lunch", "Steamed salmon, soft broccoli florets & buttered rice", "12:45 · Today", "Practiced finger-feeding broccoli · Good appetite"),
        FeedingLog("fl-4", ID_ZAYD, "Afternoon Snack", "Plain Greek yogurt with diced ripe mango", "16:00 · Today", "No allergy signs · Enjoyed sweet mango")
    )

    val immunizations = listOf(
        ImmunizationRecord("im-1", ID_ZAYD, "BCG & Polio 0", "Birth Dose", "13 Sep 2025", "Bunda Hospital", null, true),
        ImmunizationRecord("im-2", ID_ZAYD, "DTaP-Hib-HepB (Pentavalent)", "Dose 3", "18 Mar 2026", "Bunda Family Clinic", "Booster at 18 mos", true),
        ImmunizationRecord("im-3", ID_ZAYD, "PCV (Pneumococcal)", "Dose 3", "16 Sep 2026", "Bunda Family Clinic", null, true),
        ImmunizationRecord("im-4", ID_ZAYD, "MMR (Measles, Mumps, Rubella)", "Dose 1", "Scheduled: 05 Dec 2026", "Bunda Family Clinic", "05 Dec 2026", false),
        ImmunizationRecord("im-5", ID_AMINA, "DT & Measles School Booster", "Primary School", "14 Aug 2025", "School Health Clinic", "Completed", true)
    )

    val babyJournalEntries = listOf(
        BabyJournalEntry("bj-1", ID_ZAYD, "Sleep", "10:30 – 12:00", "Morning Nap (1h 30m)", "Fell asleep smoothly after storybook reading."),
        BabyJournalEntry("bj-2", ID_ZAYD, "Activity", "14:00 – 15:10", "Garden Walk & Sensory Play", "Walked on courtyard grass holding Nadia's hand and pointed at butterflies."),
        BabyJournalEntry("bj-3", ID_ZAYD, "Diaper", "15:20", "Routine Change", "Normal hydration and comfort."),
        BabyJournalEntry("bj-4", ID_ZAYD, "Bath", "17:15", "Warm Evening Bath", "Relaxed bath with lavender lotion; ready for family dinner.")
    )

    val familyEvents = listOf(
        FamilyEvent("ev-1", "Nadia's Dental Checkup", EventCategory.MEDICAL, "28 Nov 2026", "10:30", "Menteng Dental Studio", "Nadia Al-Jibal", listOf(ID_NADIA), AccessLevel.FAMILY_SHARED),
        FamilyEvent("ev-2", "Hafiz's Cardiology Follow-up", EventCategory.MEDICAL, "02 Dec 2026", "09:00", "Harapan Heart Outpatient", "Hafiz Al-Jibal (Private)", listOf(ID_HAFIZ), AccessLevel.PRIVATE_PERSONAL),
        FamilyEvent("ev-3", "Zayd's MMR Dose 1 & 15m Check", EventCategory.VACCINATION, "05 Dec 2026", "09:30", "Bunda Family Clinic · Dr. Rahma Putri", "Parents & Zayd", listOf(ID_TARIQ, ID_NADIA, ID_ZAYD), AccessLevel.PARENT_MANAGED),
        FamilyEvent("ev-4", "Monthly Education Reserve Transfer", EventCategory.FINANCE, "10 Dec 2026", "08:00", "Scheduled transfer to Children's Trust", "Parents", listOf(ID_TARIQ, ID_NADIA), AccessLevel.PARENT_MANAGED),
        FamilyEvent("ev-5", "Year-End Bandung Family Gathering", EventCategory.FAMILY, "19 Dec 2026", "All Day", "Lembang Highland Villa · Multi-generational weekend", "Entire Family", listOf(ID_TARIQ, ID_NADIA, ID_AMINA, ID_ZAYD, ID_HAFIZ), AccessLevel.FAMILY_SHARED),
        FamilyEvent("ev-6", "Nadia's Driver License Renewal Deadline", EventCategory.DOCUMENT_EXPIRY, "27 Dec 2026", "Reminder", "Expires in 38 days — prepare renewal booking", "Nadia & Tariq", listOf(ID_NADIA, ID_TARIQ), AccessLevel.PARENT_MANAGED),
        FamilyEvent("ev-7", "Family Insurance Annual Renewal", EventCategory.DOCUMENT_EXPIRY, "31 Dec 2026", "Reminder", "Policy #MJ-88412 renewal review", "Parents", listOf(ID_TARIQ, ID_NADIA), AccessLevel.FAMILY_SHARED)
    )

    val activityFeed = listOf(
        ActivityFeedItem("act-1", ActivityModule.FINANCE, "Logged Organic Groceries & Fresh Market", "$640 · Family Shared · Receipt attached", "Nadia Al-Jibal", "2 hours ago", ID_NADIA, AccessLevel.FAMILY_SHARED),
        ActivityFeedItem("act-2", ActivityModule.CHILD_DEV, "Zayd's 14-Month Growth Recorded", "Weight 10.1 kg · Length 78.5 cm · Head 46.8 cm", "Nadia Al-Jibal", "Yesterday", ID_ZAYD, AccessLevel.PARENT_MANAGED),
        ActivityFeedItem("act-3", ActivityModule.DOCUMENTS, "Document Expiry Reminder Flagged", "Amina's Passport expires in 54 days", "Vault Monitor", "2 days ago", ID_AMINA, AccessLevel.PARENT_MANAGED),
        ActivityFeedItem("act-4", ActivityModule.FINANCE, "Property Maintenance Reserve Recorded", "$1,850 · Admin-Only financial entry", "Tariq Al-Jibal", "4 days ago", ID_TARIQ, AccessLevel.ADMIN_ONLY),
        ActivityFeedItem("act-5", ActivityModule.HEALTH, "Annual Preventive Lab Results Added", "Fasting glucose & lipid panel logged", "Tariq Al-Jibal", "6 days ago", ID_TARIQ, AccessLevel.PRIVATE_PERSONAL),
        ActivityFeedItem("act-6", ActivityModule.CALENDAR, "Year-End Bandung Retreat Scheduled", "19 Dec 2026 · Assigned to Entire Family", "Tariq Al-Jibal", "1 week ago", null, AccessLevel.FAMILY_SHARED)
    )
}
