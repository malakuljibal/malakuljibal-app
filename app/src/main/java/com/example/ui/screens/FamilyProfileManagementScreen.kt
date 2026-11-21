package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.domain.model.FamilyMember
import com.example.domain.model.RelationshipType
import com.example.domain.model.UserRole
import com.example.ui.components.MalakulCard
import com.example.ui.components.MemberAvatar
import com.example.ui.components.RoleBadge
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.MalakulTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyProfileManagementScreen(
    state: MalakulUiState,
    onSelectProfile: (String) -> Unit,
    onSaveProfile: (FamilyMember) -> Unit,
    onAddProfile: (
        fullName: String,
        shortName: String,
        dateOfBirth: String,
        ageLabel: String,
        gender: String,
        relationship: RelationshipType,
        role: UserRole,
        email: String?,
        phone: String?,
        bio: String
    ) -> Unit,
    onDeleteProfile: (String) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeMember = state.selectedProfile
    var showEditDialog by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "PENGATURAN KELUARGA SAMAWA",
                            style = MaterialTheme.typography.labelSmall,
                            color = MalakulTheme.colors.gold
                        )
                        Text(
                            text = "Profil & Anggota Keluarga",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("family_management_back_btn")
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.AutoMirrored.Rounded.ArrowBack,
                            contentDescription = "Kembali"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .testTag("add_family_member_top_btn")
                            .size(48.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Add,
                            contentDescription = "Tambah Anggota",
                            tint = MalakulTheme.colors.gold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Info & Quick Add Action
            MalakulCard(
                containerColor = MalakulTheme.colors.navyHero
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.family.name,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MalakulTheme.colors.onNavyHero,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "${state.members.size} Anggota Terdaftar · Kelola identitas, nama, usia & relasi keluarga Samawa.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MalakulTheme.colors.onNavyHero.copy(alpha = 0.85f)
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_add_member_card"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MalakulTheme.colors.gold,
                            contentColor = MalakulTheme.colors.navyHero
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Tambah Anggota", fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            // Member Selection Strip Card
            Text(
                text = "PILIH ANGGOTA KELUARGA",
                style = MaterialTheme.typography.labelSmall,
                color = MalakulTheme.colors.gold,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                state.members.forEach { member ->
                    val isSelected = member.id == activeMember.id
                    Surface(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(14.dp))
                            .clickable { onSelectProfile(member.id) }
                            .testTag("manage_select_member_${member.shortName.lowercase()}"),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) MalakulTheme.colors.navyHero else MaterialTheme.colorScheme.surface,
                        border = BorderStroke(
                            width = if (isSelected) 1.5.dp else 1.dp,
                            color = if (isSelected) MalakulTheme.colors.gold else MalakulTheme.colors.cardBorder
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            MemberAvatar(member = member, size = 36.dp, isSelected = isSelected)
                            Text(
                                text = member.shortName,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MalakulTheme.colors.onNavyHero else MaterialTheme.colorScheme.onSurface,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(4.dp))

            // Detail & Edit Panel for Selected Member
            Text(
                text = "DETAIL PROFIL & INFORMASI PRIBADI",
                style = MaterialTheme.typography.labelSmall,
                color = MalakulTheme.colors.gold,
                fontWeight = FontWeight.Bold
            )

            MalakulCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("manage_member_detail_card"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MemberAvatar(member = activeMember, size = 64.dp, isSelected = true)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoleBadge(role = activeMember.role)
                            if (activeMember.id == state.activeUser.id) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MalakulTheme.colors.successContainer
                                ) {
                                    Text(
                                        text = "Akun Aktif",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MalakulTheme.colors.success
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = activeMember.fullName,
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${activeMember.relationship.displayName} · ${activeMember.ageLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = MalakulTheme.colors.cardBorder)
                Spacer(Modifier.height(12.dp))

                // Detail Information Rows
                InfoDetailItem(
                    icon = Icons.Outlined.AccountCircle,
                    label = "Nama Lengkap & Panggilan",
                    value = "${activeMember.fullName} (${activeMember.shortName})"
                )
                Spacer(Modifier.height(8.dp))
                InfoDetailItem(
                    icon = Icons.Outlined.Cake,
                    label = "Tanggal Lahir & Usia",
                    value = "${activeMember.dateOfBirth} · ${activeMember.ageLabel}"
                )
                Spacer(Modifier.height(8.dp))
                InfoDetailItem(
                    icon = Icons.Outlined.Person,
                    label = "Jenis Kelamin",
                    value = activeMember.gender
                )
                Spacer(Modifier.height(8.dp))
                InfoDetailItem(
                    icon = Icons.Outlined.Groups,
                    label = "Peran / Hubungan",
                    value = "${activeMember.relationship.displayName} (${activeMember.role.displayName})"
                )
                Spacer(Modifier.height(8.dp))
                InfoDetailItem(
                    icon = Icons.Outlined.MailOutline,
                    label = "Email Akun",
                    value = activeMember.email ?: "Tidak ada akun mandiri (Profil Terkelola)"
                )
                if (!activeMember.phone.isNullOrBlank()) {
                    Spacer(Modifier.height(8.dp))
                    InfoDetailItem(
                        icon = Icons.Outlined.Phone,
                        label = "Nomor Telepon",
                        value = activeMember.phone
                    )
                }
                if (activeMember.bio.isNotBlank()) {
                    Spacer(Modifier.height(8.dp))
                    InfoDetailItem(
                        icon = Icons.Outlined.Description,
                        label = "Bio / Catatan Pribadi",
                        value = activeMember.bio
                    )
                }

                Spacer(Modifier.height(18.dp))

                // Action Buttons: Edit and Delete
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FilledTonalButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_edit_selected_profile")
                            .defaultMinSize(minHeight = 46.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Profil")
                    }

                    if (state.members.size > 1 && activeMember.id != state.activeUser.id) {
                        OutlinedButton(
                            onClick = { showDeleteConfirmDialog = true },
                            modifier = Modifier
                                .testTag("btn_delete_selected_profile")
                                .defaultMinSize(minHeight = 46.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = MaterialTheme.colorScheme.error
                            )
                        ) {
                            Icon(Icons.Outlined.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Hapus")
                        }
                    }
                }
            }
        }
    }

    // Dialog: Edit Profil Anggota Keluarga
    if (showEditDialog) {
        FamilyMemberFormDialog(
            title = "Edit Profil Anggota",
            initialMember = activeMember,
            onDismiss = { showEditDialog = false },
            onSave = { updated ->
                onSaveProfile(updated)
                showEditDialog = false
            }
        )
    }

    // Dialog: Tambah Anggota Keluarga Baru
    if (showAddDialog) {
        FamilyMemberAddDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { fullName, shortName, dob, age, gender, rel, role, email, phone, bio ->
                onAddProfile(fullName, shortName, dob, age, gender, rel, role, email, phone, bio)
                showAddDialog = false
            }
        )
    }

    // Dialog: Konfirmasi Hapus Anggota
    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Hapus Anggota Keluarga") },
            text = { Text("Apakah Anda yakin ingin menghapus data profil ${activeMember.fullName}? Tindakan ini tidak dapat dibatalkan.") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProfile(activeMember.id)
                        showDeleteConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Ya, Hapus")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun InfoDetailItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MalakulTheme.colors.gold,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemberFormDialog(
    title: String,
    initialMember: FamilyMember,
    onDismiss: () -> Unit,
    onSave: (FamilyMember) -> Unit
) {
    var fullName by remember { mutableStateOf(initialMember.fullName) }
    var shortName by remember { mutableStateOf(initialMember.shortName) }
    var dateOfBirth by remember { mutableStateOf(initialMember.dateOfBirth) }
    var ageLabel by remember { mutableStateOf(initialMember.ageLabel) }
    var gender by remember { mutableStateOf(initialMember.gender) }
    var relationship by remember { mutableStateOf(initialMember.relationship) }
    var role by remember { mutableStateOf(initialMember.role) }
    var email by remember { mutableStateOf(initialMember.email ?: "") }
    var phone by remember { mutableStateOf(initialMember.phone ?: "") }
    var bio by remember { mutableStateOf(initialMember.bio) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_full_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = shortName,
                        onValueChange = { shortName = it },
                        label = { Text("Panggilan") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_short_name")
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender (L/P)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_gender")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Tgl Lahir (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("input_dob")
                    )
                    OutlinedTextField(
                        value = ageLabel,
                        onValueChange = { ageLabel = it },
                        label = { Text("Usia / Umur") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_age")
                    )
                }

                // Relasi Dropdown / Selection
                Text(
                    text = "Hubungan Keluarga:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedRow(
                    options = listOf(
                        RelationshipType.HUSBAND to "Suami",
                        RelationshipType.WIFE to "Istri",
                        RelationshipType.SON to "Anak (L)",
                        RelationshipType.DAUGHTER to "Anak (P)",
                        RelationshipType.RELATIVE to "Lainnya"
                    ),
                    selected = relationship,
                    onSelect = { relationship = it }
                )

                // Role Dropdown / Selection
                Text(
                    text = "Peran Hak Akses:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedRow(
                    options = listOf(
                        UserRole.SUPER_ADMIN to "Admin",
                        UserRole.PARENT to "Orang Tua",
                        UserRole.FAMILY_MEMBER to "Anggota",
                        UserRole.CHILD_PROFILE to "Anak"
                    ),
                    selected = role,
                    onSelect = { role = it }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_email")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor Telepon (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_phone")
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio / Catatan Profil") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("input_bio")
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cleanShort = shortName.trim().ifBlank { fullName.trim().take(8) }
                            val initials = cleanShort.take(2).uppercase()
                            val updated = initialMember.copy(
                                fullName = fullName.trim(),
                                shortName = cleanShort,
                                initials = initials,
                                dateOfBirth = dateOfBirth.trim(),
                                ageLabel = ageLabel.trim(),
                                gender = gender.trim(),
                                relationship = relationship,
                                role = role,
                                email = email.trim().ifBlank { null },
                                phone = phone.trim().ifBlank { null },
                                bio = bio.trim()
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("btn_save_member_dialog")
                    ) {
                        Text("Simpan Perubahan")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FamilyMemberAddDialog(
    onDismiss: () -> Unit,
    onAdd: (
        fullName: String,
        shortName: String,
        dateOfBirth: String,
        ageLabel: String,
        gender: String,
        relationship: RelationshipType,
        role: UserRole,
        email: String?,
        phone: String?,
        bio: String
    ) -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var shortName by remember { mutableStateOf("") }
    var dateOfBirth by remember { mutableStateOf("2024-01-01") }
    var ageLabel by remember { mutableStateOf("1 Tahun") }
    var gender by remember { mutableStateOf("Laki-laki") }
    var relationship by remember { mutableStateOf(RelationshipType.SON) }
    var role by remember { mutableStateOf(UserRole.CHILD_PROFILE) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Tambah Anggota Keluarga",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nama Lengkap *") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_input_full_name")
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = shortName,
                        onValueChange = { shortName = it },
                        label = { Text("Panggilan") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("add_input_short_name")
                    )
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { gender = it },
                        label = { Text("Gender") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("add_input_gender")
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Tgl Lahir (YYYY-MM-DD)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("add_input_dob")
                    )
                    OutlinedTextField(
                        value = ageLabel,
                        onValueChange = { ageLabel = it },
                        label = { Text("Usia / Umur") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("add_input_age")
                    )
                }

                Text(
                    text = "Hubungan Keluarga:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedRow(
                    options = listOf(
                        RelationshipType.HUSBAND to "Suami",
                        RelationshipType.WIFE to "Istri",
                        RelationshipType.SON to "Anak (L)",
                        RelationshipType.DAUGHTER to "Anak (P)",
                        RelationshipType.RELATIVE to "Kerabat"
                    ),
                    selected = relationship,
                    onSelect = { relationship = it }
                )

                Text(
                    text = "Peran Akun:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedRow(
                    options = listOf(
                        UserRole.SUPER_ADMIN to "Admin",
                        UserRole.PARENT to "Orang Tua",
                        UserRole.FAMILY_MEMBER to "Anggota",
                        UserRole.CHILD_PROFILE to "Anak"
                    ),
                    selected = role,
                    onSelect = { role = it }
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_input_email")
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = { Text("Nomor Telepon (Opsional)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("add_input_phone")
                )

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("Bio Singkat") },
                    minLines = 2,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth().testTag("add_input_bio")
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (fullName.isNotBlank()) {
                                onAdd(
                                    fullName,
                                    shortName.ifBlank { fullName },
                                    dateOfBirth,
                                    ageLabel,
                                    gender,
                                    relationship,
                                    role,
                                    email.ifBlank { null },
                                    phone.ifBlank { null },
                                    bio
                                )
                            }
                        },
                        enabled = fullName.isNotBlank(),
                        modifier = Modifier.testTag("btn_submit_add_member")
                    ) {
                        Text("Tambah")
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> SingleChoiceSegmentedRow(
    options: List<Pair<T, String>>,
    selected: T,
    onSelect: (T) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        options.forEach { (option, label) ->
            val isCurrent = option == selected
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { onSelect(option) },
                shape = RoundedCornerShape(10.dp),
                color = if (isCurrent) MalakulTheme.colors.navyHero else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = BorderStroke(
                    width = 1.dp,
                    color = if (isCurrent) MalakulTheme.colors.gold else MalakulTheme.colors.cardBorder
                )
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isCurrent) MalakulTheme.colors.onNavyHero else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }
    }
}
