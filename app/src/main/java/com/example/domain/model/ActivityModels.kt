package com.example.domain.model

enum class EventCategory(val label: String) {
    MEDICAL("Medical"),
    VACCINATION("Vaccination"),
    DOCUMENT_EXPIRY("Document Expiry"),
    FINANCE("Financial Deadline"),
    FAMILY("Family Event"),
    PERSONAL("Personal Reminder")
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
