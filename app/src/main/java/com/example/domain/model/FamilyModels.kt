package com.example.domain.model

import androidx.annotation.DrawableRes

enum class RelationshipType(val displayName: String) {
    HUSBAND("Suami & Ayah"),
    WIFE("Istri & Ibu"),
    SON("Anak Laki-laki"),
    DAUGHTER("Anak Perempuan"),
    FATHER("Ayah / Kakek"),
    MOTHER("Ibu / Nenek"),
    RELATIVE("Kerabat / Keluarga")
}

enum class UserRole(
    val displayName: String,
    val shortBadge: String,
    val description: String
) {
    SUPER_ADMIN(
        displayName = "Administrator Keluarga",
        shortBadge = "Super Admin",
        description = "Akses penuh pengaturan keluarga, izin anggota, keuangan bersama, dan brankas dokumen."
    ),
    PARENT(
        displayName = "Orang Tua",
        shortBadge = "Orang Tua",
        description = "Mengelola profil pribadi, data rumah tangga bersama, dan profil anak."
    ),
    FAMILY_MEMBER(
        displayName = "Anggota Keluarga",
        shortBadge = "Anggota",
        description = "Mengakses profil pribadi, agenda keluarga bersama, dan catatan yang diizinkan."
    ),
    CHILD_PROFILE(
        displayName = "Profil Anak",
        shortBadge = "Anak",
        description = "Dikelola oleh orang tua tanpa akun mandiri; mencakup pemantauan tumbuh kembang."
    )
}

enum class AccessLevel(val label: String) {
    FAMILY_SHARED("Bersama Keluarga"),
    ADMIN_ONLY("Khusus Admin"),
    PRIVATE_PERSONAL("Pribadi"),
    PARENT_MANAGED("Dikelola Orang Tua")
}

enum class AppModule(val label: String) {
    COMMAND_CENTER("Pusat Kendali Keluarga"),
    PROFILES("Profil Anggota"),
    FINANCE("Keuangan & Anggaran"),
    HEALTH("Kesehatan & Medis"),
    DOCUMENTS("Brankas Dokumen"),
    CHILD_DEVELOPMENT("Tumbuh Kembang Anak"),
    CALENDAR("Kalender Keluarga")
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
