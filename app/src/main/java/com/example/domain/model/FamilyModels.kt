package com.example.domain.model

import androidx.annotation.DrawableRes

enum class RelationshipType(val displayName: String) {
    HUSBAND("Husband & Father"),
    WIFE("Wife & Mother"),
    SON("Son"),
    DAUGHTER("Daughter"),
    FATHER("Father (Grandfather)"),
    MOTHER("Mother (Grandmother)"),
    RELATIVE("Relative")
}

enum class UserRole(
    val displayName: String,
    val shortBadge: String,
    val description: String
) {
    SUPER_ADMIN(
        displayName = "Family Administrator",
        shortBadge = "Super Admin",
        description = "Full household command, permissions governance, shared finances, and vault oversight."
    ),
    PARENT(
        displayName = "Parent",
        shortBadge = "Parent",
        description = "Manages own profile, shared household data, and authorized child profiles."
    ),
    FAMILY_MEMBER(
        displayName = "Family Member",
        shortBadge = "Member",
        description = "Accesses own personal profile, shared family events, and explicitly permitted records."
    ),
    CHILD_PROFILE(
        displayName = "Child Profile",
        shortBadge = "Child",
        description = "Managed by authorized parents without independent login; includes pediatric tracking."
    )
}

enum class AccessLevel(val label: String) {
    FAMILY_SHARED("Family Shared"),
    ADMIN_ONLY("Admin Only"),
    PRIVATE_PERSONAL("Private"),
    PARENT_MANAGED("Parent Managed")
}

enum class AppModule(val label: String) {
    COMMAND_CENTER("Family Command Center"),
    PROFILES("Personal Profiles"),
    FINANCE("Finance & Budgeting"),
    HEALTH("Health & Medical Records"),
    DOCUMENTS("Document Vault"),
    CHILD_DEVELOPMENT("Baby & Child Development"),
    CALENDAR("Family Calendar")
}

data class ModulePermission(
    val module: AppModule,
    val canView: Boolean,
    val canEdit: Boolean,
    val scopeNote: String
)

data class Family(
    val id: String,
    val name: String,
    val motto: String,
    val description: String,
    val createdDate: String,
    val adminMemberId: String,
    val location: String
)

data class FamilyMember(
    val id: String,
    val familyId: String,
    val fullName: String,
    val shortName: String,
    val initials: String,
    @DrawableRes val avatarRes: Int? = null,
    val dateOfBirth: String,
    val ageLabel: String,
    val gender: String,
    val relationship: RelationshipType,
    val role: UserRole,
    val email: String?,
    val phone: String?,
    val hasIndependentAccount: Boolean,
    val managedByParentIds: List<String> = emptyList(),
    val bio: String,
    val financialContributionNote: String,
    val monthlyAllowanceOrShare: String,
    val permissions: List<ModulePermission>,
    val personalHighlights: List<String>
)
