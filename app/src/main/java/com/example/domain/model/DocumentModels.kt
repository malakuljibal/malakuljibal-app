package com.example.domain.model

enum class DocumentCategory(val displayName: String) {
    ALL("All"),
    IDENTITY("Identity Cards"),
    BIRTH_CERT("Birth Certificates"),
    MARRIAGE_CERT("Marriage Certificates"),
    PASSPORT("Passports"),
    EDUCATION("Education"),
    INSURANCE("Insurance"),
    PROPERTY("Property"),
    VEHICLE("Vehicle"),
    FINANCIAL("Financial"),
    MEDICAL("Medical"),
    OTHER("Other")
}

enum class DocumentStatus(val label: String) {
    ACTIVE("Valid"),
    EXPIRING_SOON("Expiring Soon"),
    ARCHIVED("Archived")
}

data class FamilyDocument(
    val id: String,
    val title: String,
    val category: DocumentCategory,
    val ownerMemberId: String,
    val ownerName: String,
    val issueDate: String,
    val expirationDate: String?,
    val daysUntilExpiry: Int?,
    val description: String,
    val tags: List<String>,
    val uploadedBy: String,
    val uploadTimestamp: String,
    val fileFormat: String,
    val fileSizeLabel: String,
    val versionLabel: String,
    val status: DocumentStatus,
    val accessLevel: AccessLevel,
    val authorizedViewerIds: List<String> = emptyList(),
    val recentAuditEntries: List<String> = emptyList()
)
