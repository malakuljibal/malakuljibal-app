package com.example.domain.model

enum class EventCategory(val label: String) {
    MEDICAL("Pemeriksaan Medis"),
    VACCINATION("Imunisasi / Vaksin"),
    DOCUMENT_EXPIRY("Masa Berlaku Dokumen"),
    FINANCE("Tenggat Keuangan"),
    FAMILY("Acara Keluarga"),
    PERSONAL("Pengingat Pribadi")
}

data class FamilyEvent(
    val id: String,
    val title: String,
    val category: EventCategory,
    val dateLabel: String,
    val timeLabel: String,
    val locationOrNote: String,
    val assignedScope: String,
    val assignedMemberIds: List<String>,
    val accessLevel: AccessLevel
)

enum class ActivityModule(val label: String) {
    FINANCE("Finance"),
    HEALTH("Health"),
    DOCUMENTS("Documents"),
    CHILD_DEV("Child Growth"),
    CALENDAR("Calendar")
}

data class ActivityFeedItem(
    val id: String,
    val module: ActivityModule,
    val title: String,
    val subtitle: String,
    val actorName: String,
    val timestampLabel: String,
    val relatedMemberId: String?,
    val accessLevel: AccessLevel
)

data class FamilyAnnouncement(
    val id: String,
    val title: String,
    val message: String,
    val postedBy: String,
    val dateLabel: String
)
