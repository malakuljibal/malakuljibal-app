package com.example.domain.model

enum class DocumentCategory(val displayName: String) {
    ALL("Semua"),
    IDENTITY("Kartu Identitas"),
    BIRTH_CERT("Akta Kelahiran"),
    MARRIAGE_CERT("Buku / Akta Nikah"),
    PASSPORT("Paspor"),
    EDUCATION("Pendidikan / Ijazah"),
    INSURANCE("Asuransi"),
    PROPERTY("Sertifikat Properti"),
    VEHICLE("Surat Kendaraan"),
    FINANCIAL("Keuangan / Pajak"),
    MEDICAL("Rekam Medis"),
    OTHER("Lainnya")
}

enum class DocumentStatus(val label: String) {
    ACTIVE("Berlaku"),
    EXPIRING_SOON("Segera Kedaluwarsa"),
    ARCHIVED("Diarsipkan")
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
