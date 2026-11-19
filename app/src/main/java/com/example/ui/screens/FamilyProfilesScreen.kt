package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.UserRole
import com.example.domain.rbac.AccessControl
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.MalakulTheme

@Composable
fun FamilyProfilesScreen(
    state: MalakulUiState,
    onSelectProfile: (String) -> Unit,
    onOpenChildDevelopment: (String) -> Unit,
    onOpenMemberHealth: (String) -> Unit,
    onOpenDocuments: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    val selected = state.selectedProfile
    val medicalProfile = state.medicalProfiles.firstOrNull { it.memberId == selected.id }
    val canViewMedical = AccessControl.canViewMedicalProfile(activeUser, selected, medicalProfile)
    val childProfile = state.childProfiles.firstOrNull { it.memberId == selected.id }

    val memberDocs = state.documents.filter { it.ownerMemberId == selected.id }
    val accessibleDocs = memberDocs.filter { AccessControl.canViewDocument(activeUser, it) }
    val hiddenDocsCount = memberDocs.size - accessibleDocs.size

    val memberActivity = state.visibleActivityFeed.filter { it.relatedMemberId == selected.id }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("family_profiles_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "PERSONAL FAMILY PROFILES",
                    style = MaterialTheme.typography.labelSmall,
                    color = MalakulTheme.colors.gold
                )
                Text(
                    text = state.family.name,
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Select any household member to inspect personal information, authorized records, and privacy boundaries.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Family Member Selector Strip
        item {
            FamilyMemberSelectorStrip(
                members = state.members,
                selectedMemberId = selected.id,
                onSelectMember = onSelectProfile
            )
        }

        // Selected Member Hero Identity Card
        item {
            MalakulCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("selected_profile_card"),
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MemberAvatar(member = selected, size = 72.dp, isSelected = true)
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoleBadge(role = selected.role)
                            if (selected.id == activeUser.id) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MalakulTheme.colors.successContainer
                                ) {
                                    Text(
                                        text = "Active Session",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MalakulTheme.colors.success
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = selected.fullName,
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${selected.relationship.displayName} · ${selected.ageLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(Modifier.height(14.dp))
                Text(
                    text = selected.bio,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(14.dp))
                HorizontalDivider(color = MalakulTheme.colors.cardBorder)
                Spacer(Modifier.height(12.dp))

                ProfileDetailRow(Icons.Outlined.Cake, "Date of Birth", "${selected.dateOfBirth} (${selected.gender})")
                Spacer(Modifier.height(8.dp))
                ProfileDetailRow(
                    Icons.Outlined.MailOutline,
                    "Account & Email",
                    selected.email ?: "Managed Child Profile (No independent login)"
                )
                if (selected.phone != null) {
                    Spacer(Modifier.height(8.dp))
                    ProfileDetailRow(Icons.Outlined.Phone, "Contact Phone", selected.phone)
                }
                if (selected.managedByParentIds.isNotEmpty()) {
                    val guardianNames = state.members
                        .filter { selected.managedByParentIds.contains(it.id) }
                        .joinToString(" & ") { it.shortName }
                    Spacer(Modifier.height(8.dp))
                    ProfileDetailRow(Icons.Outlined.VerifiedUser, "Authorized Guardians", guardianNames)
                }
            }
        }

        // Dedicated Pediatric Development Section for Child Profiles
        if (selected.role == UserRole.CHILD_PROFILE && childProfile != null) {
            item {
                val canSeeChild = AccessControl.canViewChildProfile(activeUser, selected)
                if (canSeeChild) {
                    MalakulCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("profile_pediatric_card"),
                        containerColor = MalakulTheme.colors.goldContainer.copy(alpha = 0.35f),
                        onClick = { onOpenChildDevelopment(selected.id) }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "PEDIATRIC HEALTH & DEVELOPMENT",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MalakulTheme.colors.onGoldContainer
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "${childProfile.ageSummary} · Last Measured ${childProfile.lastMeasuredDate}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            FilledTonalButton(
                                onClick = { onOpenChildDevelopment(selected.id) },
                                modifier = Modifier
                                    .testTag("open_child_tracker_button")
                                    .defaultMinSize(minHeight = 48.dp)
                            ) {
                                Text("Tracker")
                                Spacer(Modifier.width(4.dp))
                                Icon(Icons.Rounded.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetricStatBlock("Berat", "${childProfile.latestWeightKg} kg", modifier = Modifier.weight(1f))
                            MetricStatBlock("Tinggi", "${childProfile.latestLengthCm} cm", modifier = Modifier.weight(1f))
                            MetricStatBlock("Lingkar Kpl", "${childProfile.latestHeadCircumferenceCm} cm", modifier = Modifier.weight(1f))
                        }
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Next Immunization: ${childProfile.nextImmunizationSummary}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    RestrictedAccessCard(
                        title = "Pediatric Development Restricted",
                        reason = "Child growth and developmental logs are accessible only to authorized parents and guardians."
                    )
                }
            }
        }

        // Personal Health Summary (Private by Default)
        item {
            SectionHeader(
                title = "Personal Health & Medical",
                subtitle = "Medical records are private by default",
                actionLabel = if (canViewMedical) "Full Health" else null,
                onActionClick = if (canViewMedical) ({ onOpenMemberHealth(selected.id) }) else null
            )
            Spacer(Modifier.height(8.dp))
            if (canViewMedical && medicalProfile != null) {
                MalakulCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onOpenMemberHealth(selected.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        PrivacyBadge(accessLevel = medicalProfile.accessLevel)
                        Text(
                            text = "Blood Type ${medicalProfile.bloodType}",
                            style = MaterialTheme.typography.titleSmall,
                            color = MalakulTheme.colors.healthAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    ProfileDetailRow(
                        Icons.Outlined.WarningAmber,
                        "Allergies",
                        medicalProfile.allergies.joinToString(", ")
                    )
                    Spacer(Modifier.height(6.dp))
                    ProfileDetailRow(
                        Icons.Outlined.MedicalInformation,
                        "Conditions",
                        medicalProfile.conditions.joinToString(", ")
                    )
                    Spacer(Modifier.height(6.dp))
                    ProfileDetailRow(
                        Icons.Outlined.LocalHospital,
                        "Primary Physician",
                        medicalProfile.primaryPhysician
                    )
                }
            } else {
                RestrictedAccessCard(
                    title = "Private Medical Profile",
                    reason = "${selected.shortName}'s medical records are private by default. Switch active session to ${selected.shortName} in Profile to view."
                )
            }
        }

        // Financial Participation
        item {
            SectionHeader(
                title = "Financial Participation",
                subtitle = "Role-scoped household finance involvement"
            )
            Spacer(Modifier.height(8.dp))
            if (AccessControl.canViewFinanceOverview(activeUser) || selected.id == activeUser.id) {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selected.monthlyAllowanceOrShare,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MalakulTheme.colors.gold
                        )
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(
                        text = selected.financialContributionNote,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                RestrictedAccessCard(
                    title = "Financial Details Protected",
                    reason = "Personal financial participation is hidden from non-authorized household roles."
                )
            }
        }

        // Personal Documents Summary
        item {
            SectionHeader(
                title = "Personal Documents",
                subtitle = "${accessibleDocs.size} accessible · $hiddenDocsCount restricted",
                actionLabel = "Open Vault",
                onActionClick = onOpenDocuments
            )
            Spacer(Modifier.height(8.dp))
            MalakulCard(modifier = Modifier.fillMaxWidth()) {
                if (accessibleDocs.isEmpty()) {
                    Text(
                        text = "No accessible personal documents for your current role.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    accessibleDocs.forEachIndexed { idx, doc ->
                        if (idx > 0) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 10.dp),
                                color = MalakulTheme.colors.cardBorder
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = doc.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${doc.category.displayName} · ${doc.fileFormat} · ${doc.expirationDate ?: "No expiry"}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            PrivacyBadge(accessLevel = doc.accessLevel)
                        }
                    }
                }
                if (hiddenDocsCount > 0) {
                    Spacer(Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MalakulTheme.colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(15.dp)
                            )
                            Text(
                                text = "$hiddenDocsCount private document(s) hidden by record-level privacy rules.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        // Personal Highlights & Activity History
        item {
            SectionHeader(
                title = "Personal Highlights & History",
                subtitle = "Recent milestones and recorded activity"
            )
            Spacer(Modifier.height(8.dp))
            MalakulCard(modifier = Modifier.fillMaxWidth()) {
                selected.personalHighlights.forEachIndexed { idx, highlight ->
                    if (idx > 0) Spacer(Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.CheckCircleOutline,
                            contentDescription = null,
                            tint = MalakulTheme.colors.gold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                if (memberActivity.isNotEmpty()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 12.dp),
                        color = MalakulTheme.colors.cardBorder
                    )
                    memberActivity.forEach { act ->
                        Text(
                            text = "${act.title} · ${act.timestampLabel}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(4.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MalakulTheme.colors.gold,
            modifier = Modifier
                .padding(top = 2.dp)
                .size(18.dp)
        )
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
