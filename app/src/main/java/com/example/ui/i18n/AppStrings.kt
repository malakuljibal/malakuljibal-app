package com.example.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

data class AppStrings(
    // App identity & Brand
    val appName: String,
    val brandTitle: String,
    val brandTagline: String,

    // Navigation
    val navHome: String,
    val navFamily: String,
    val navFinance: String,
    val navHealth: String,
    val navDocuments: String,
    val navProfile: String,
    val navCalendar: String,
    val navChildDev: String,

    // Settings & Appearance
    val settingsTitle: String,
    val settingsSubtitle: String,
    val languageSectionTitle: String,
    val languageSectionSubtitle: String,
    val appearanceSectionTitle: String,
    val appearanceSectionSubtitle: String,
    val themeLight: String,
    val themeDark: String,
    val themeSystem: String,

    // Account & Family Management
    val activeMemberTitle: String,
    val switchProfile: String,
    val activeSession: String,
    val manageFamilyProfiles: String,
    val manageFamilySubtitle: String,
    val editProfile: String,
    val addFamilyMember: String,
    val deleteProfile: String,
    val saveProfile: String,

    // Common actions & status
    val save: String,
    val cancel: String,
    val close: String,
    val back: String,
    val search: String,
    val connectionStatus: String,
    val apiConfigTitle: String,
    val apiConfigSubtitle: String,
    val saveUrl: String,
    val urlSavedToast: String,
    val activeRolePermissions: String,
    val sessionManagement: String,
    val signOut: String,
    val demoModeBadge: String,
    val demoModeSubtitle: String,
    val medicalDisclaimer: String,
    val privacyRestrictedTitle: String,
    val privacyRestrictedBody: String
)

val IndonesianStrings = AppStrings(
    appName = "Samawa",
    brandTitle = "SAMAWA",
    brandTagline = "Keluarga Kita. Kesehatan Kita. Warisan Kita.",
    navHome = "Beranda",
    navFamily = "Keluarga",
    navFinance = "Keuangan",
    navHealth = "Kesehatan",
    navDocuments = "Brankas",
    navProfile = "Profil",
    navCalendar = "Kalender",
    navChildDev = "Tumbuh Kembang",
    settingsTitle = "Pengaturan & Profil",
    settingsSubtitle = "Kelola akun keluarga, tampilan tema, dan bahasa",
    languageSectionTitle = "Bahasa Aplikasi",
    languageSectionSubtitle = "Pilih bahasa tampilan antarmuka Samawa",
    appearanceSectionTitle = "Tampilan Tema",
    appearanceSectionSubtitle = "Tema Terang Warm Ivory & Gelap Obsidian Navy",
    themeLight = "Terang",
    themeDark = "Gelap",
    themeSystem = "Sistem",
    activeMemberTitle = "Akun Anggota Aktif",
    switchProfile = "Beralih",
    activeSession = "Sesi Aktif",
    manageFamilyProfiles = "Kelola Profil & Anggota Keluarga",
    manageFamilySubtitle = "Pilih anggota keluarga untuk melihat atau memperbarui informasi pribadi",
    editProfile = "Edit Profil",
    addFamilyMember = "Tambah Anggota",
    deleteProfile = "Hapus Profil",
    saveProfile = "Simpan Profil",
    save = "Simpan",
    cancel = "Batal",
    close = "Tutup",
    back = "Kembali",
    search = "Cari",
    connectionStatus = "Status Koneksi",
    apiConfigTitle = "Konfigurasi Server REST API",
    apiConfigSubtitle = "Endpoint REST API PHP 8.x Hostinger",
    saveUrl = "Simpan URL",
    urlSavedToast = "Base URL berhasil disimpan",
    activeRolePermissions = "Izin Peran Aktif",
    sessionManagement = "Manajemen Sesi",
    signOut = "Keluar / Ganti Akun",
    demoModeBadge = "MODE KELUARGA · AKTIF",
    demoModeSubtitle = "Aplikasi keluarga terpadu — terhubung ke server api.malakuljibal.com.",
    medicalDisclaimer = "Catatan kesehatan dan tumbuh kembang anak ini hanya untuk pengorganisasian arsip pribadi keluarga, bukan sebagai pengganti diagnosis atau nasihat medis klinis.",
    privacyRestrictedTitle = "Catatan Terbatas / Pribadi",
    privacyRestrictedBody = "Bagian ini bersifat privat dan hanya dapat diakses oleh pemilik catatan atau anggota keluarga yang diberikan izin khusus."
)

val EnglishStrings = AppStrings(
    appName = "Samawa",
    brandTitle = "SAMAWA",
    brandTagline = "Our Family. Our Health. Our Legacy.",
    navHome = "Home",
    navFamily = "Family",
    navFinance = "Finance",
    navHealth = "Health",
    navDocuments = "Vault",
    navProfile = "Profile",
    navCalendar = "Calendar",
    navChildDev = "Child Dev",
    settingsTitle = "Settings & Profile",
    settingsSubtitle = "Manage family account, theme appearance, and language",
    languageSectionTitle = "App Language",
    languageSectionSubtitle = "Select Samawa interface display language",
    appearanceSectionTitle = "Appearance",
    appearanceSectionSubtitle = "Warm Ivory Light & Obsidian Navy Dark themes",
    themeLight = "Light",
    themeDark = "Dark",
    themeSystem = "System",
    activeMemberTitle = "Active Member Account",
    switchProfile = "Switch",
    activeSession = "Active Session",
    manageFamilyProfiles = "Manage Profiles & Family Members",
    manageFamilySubtitle = "Select household member to inspect or update personal information",
    editProfile = "Edit Profile",
    addFamilyMember = "Add Member",
    deleteProfile = "Delete Profile",
    saveProfile = "Save Profile",
    save = "Save",
    cancel = "Cancel",
    close = "Close",
    back = "Back",
    search = "Search",
    connectionStatus = "Connection Status",
    apiConfigTitle = "REST API Server Config",
    apiConfigSubtitle = "Live Hostinger PHP 8.x REST API endpoint",
    saveUrl = "Save URL",
    urlSavedToast = "Base URL saved",
    activeRolePermissions = "Active Role Permissions",
    sessionManagement = "Session Management",
    signOut = "Sign Out / Switch Account",
    demoModeBadge = "FAMILY MODE · ACTIVE",
    demoModeSubtitle = "Integrated family app — connected to api.malakuljibal.com server.",
    medicalDisclaimer = "Health and child growth records are strictly for private family record keeping, not a replacement for clinical medical diagnosis or advice.",
    privacyRestrictedTitle = "Restricted / Private Records",
    privacyRestrictedBody = "This section is confidential and only accessible by the record owner or family members with explicit permission."
)

fun getAppStrings(language: AppLanguage): AppStrings {
    return when (language) {
        AppLanguage.INDONESIAN -> IndonesianStrings
        AppLanguage.ENGLISH -> EnglishStrings
    }
}

val LocalAppStrings = staticCompositionLocalOf { IndonesianStrings }

val AppStringsCurrent: AppStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalAppStrings.current
