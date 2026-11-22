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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.domain.model.MeasurementType
import com.example.domain.model.UserRole
import com.example.domain.rbac.AccessControl
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import androidx.compose.foundation.clickable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HealthPreviewScreen(
    state: MalakulUiState,
    onRefresh: () -> Unit,
    onDismissFeedback: () -> Unit,
    onAddHealthRecord: (diagnosis: String, hospitalName: String, doctorName: String, notes: String, recordDate: String) -> Unit,
    onSelectHealthMember: (String) -> Unit,
    onOpenChildDevelopment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    val targetMember = state.selectedHealthMember
    val medicalProfile = state.medicalProfiles.firstOrNull { it.memberId == targetMember.id }
    val canView = state.isLiveMode || AccessControl.canViewMedicalProfile(activeUser, targetMember, medicalProfile)

    val measurements = state.healthMeasurements.filter { it.memberId == targetMember.id }
    val records = if (state.isLiveMode) state.medicalRecords else state.medicalRecords.filter { it.memberId == targetMember.id }
    val medications = state.medications.filter { it.memberId == targetMember.id }
    val labResults = state.labResults.filter { it.memberId == targetMember.id }

    var showAddDialog by remember { mutableStateOf(false) }

    if (showAddDialog) {
        AddHealthRecordDialog(
            isSubmitting = state.isSubmitting,
            onDismiss = { showAddDialog = false },
            onSubmit = { diag, hosp, doc, notes, date ->
                onAddHealthRecord(diag, hosp, doc, notes, date)
                showAddDialog = false
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("health_preview_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isLiveMode) "MODUL KESEHATAN · AKTIF" else "MODUL KESEHATAN KELUARGA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MalakulTheme.colors.healthAccent
                    )
                    Text(
                        text = "Rekam Medis & Kesehatan",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (state.isLiveMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .testTag("health_refresh_btn")
                                .size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh health records")
                        }
                        FilledTonalButton(
                            onClick = { showAddDialog = true },
                            enabled = !state.isSubmitting,
                            modifier = Modifier
                                .testTag("health_add_btn")
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Record")
                        }
                    }
                }
            }
        }

        if (state.isLoadingHealth || state.isSubmitting) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(999.dp)),
                    color = MalakulTheme.colors.healthAccent
                )
            }
        }

        if (state.globalError != null || state.actionMessage != null) {
            item {
                ApiFeedbackBanner(
                    errorMessage = state.globalError,
                    successMessage = state.actionMessage,
                    onRetry = if (state.isLiveMode) onRefresh else null,
                    onDismiss = onDismissFeedback
                )
            }
        }

        // Non-diagnostic disclaimer banner
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MalakulTheme.colors.healthContainer.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, MalakulTheme.colors.healthAccent.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Info,
                        contentDescription = null,
                        tint = MalakulTheme.colors.healthAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.medical_disclaimer),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        // Child Growth Quick Entry
        item {
            MalakulCard(
                containerColor = MalakulTheme.colors.goldContainer.copy(alpha = 0.35f),
                onClick = { onOpenChildDevelopment(state.selectedChildMemberId) }
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("BABY HEALTH & GROWTH", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.onGoldContainer)
                        Text("Open Pediatric Growth Charts & Baby Measurements", style = MaterialTheme.typography.titleSmall)
                    }
                    Icon(Icons.Rounded.ChevronRight, contentDescription = null, tint = MalakulTheme.colors.onGoldContainer)
                }
            }
        }

        // Live Consultation & Medical Records Timeline
        item {
            SectionHeader(
                title = "Medical Records",
                subtitle = if (state.isLiveMode) "${records.size} synced records from /health/records"
                else "Consultations & professional recommendations",
                actionLabel = if (state.isLiveMode) "+ Add" else null,
                onActionClick = if (state.isLiveMode) ({ showAddDialog = true }) else null
            )
        }

        if (records.isEmpty()) {
            item {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.MedicalServices,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No medical records found", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (state.isLiveMode) "Tap '+ Record' to add a consultation or diagnosis."
                            else "Select another family member to view records.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(records, key = { it.id }) { rec ->
                MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(rec.consultationDate, style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                        PrivacyBadge(accessLevel = rec.accessLevel)
                    }
                    Spacer(Modifier.height(6.dp))
                    Text(rec.recordedDiagnosis, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                    Text("${rec.facility} · ${rec.doctorName}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    HealthInfoLine("Notes & Recommendations", rec.doctorRecommendations)
                }
            }
        }

        // Offline Demo Member Profile / Vitals (shown in Offline Demo Mode)
        if (!state.isLiveMode) {
            item {
                FamilyMemberSelectorStrip(
                    members = state.members,
                    selectedMemberId = targetMember.id,
                    onSelectMember = onSelectHealthMember
                )
            }

            if (!canView || medicalProfile == null) {
                item {
                    RestrictedAccessCard(
                        title = "${targetMember.shortName}'s Medical Records Are Private",
                        reason = "Medical profiles are private by default and require explicit owner consent."
                    )
                }
            } else {
                item {
                    MalakulCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(targetMember.fullName, style = MaterialTheme.typography.titleMedium)
                                Text("Blood Type ${medicalProfile.bloodType} · ${medicalProfile.primaryPhysician}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            PrivacyBadge(accessLevel = medicalProfile.accessLevel)
                        }
                        Spacer(Modifier.height(10.dp))
                        HealthInfoLine("Allergies", medicalProfile.allergies.joinToString(", "))
                        Spacer(Modifier.height(4.dp))
                        HealthInfoLine("Emergency Contact", "${medicalProfile.emergencyContactName} · ${medicalProfile.emergencyContactPhone}")
                    }
                }
                if (measurements.isNotEmpty()) {
                    item {
                        val primaryType = if (measurements.any { it.type == MeasurementType.WEIGHT })
                            MeasurementType.WEIGHT else measurements.first().type
                        val chartPoints = measurements.filter { it.type == primaryType }.map { it.dateLabel to it.numericValue }
                        MalakulCard(modifier = Modifier.fillMaxWidth()) {
                            Text("${primaryType.label} History (${primaryType.unit})", style = MaterialTheme.typography.titleMedium)
                            Spacer(Modifier.height(12.dp))
                            if (chartPoints.size >= 2) {
                                LineTrendChart(points = chartPoints, lineColor = MalakulTheme.colors.healthAccent, unitSuffix = "")
                            }
                        }
                    }
                }
                if (medications.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Medications", subtitle = "Prescribed regimens")
                    }
                    items(medications, key = { it.id }) { med ->
                        MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Text("${med.name} · ${med.dosage}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${med.frequency} · ${med.prescribingDoctor}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (labResults.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Lab Results", subtitle = "Laboratory-provided reference ranges")
                    }
                    items(labResults, key = { it.id }) { lab ->
                        MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(lab.testName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(lab.recordedValue, style = MaterialTheme.typography.titleSmall.copy(fontFamily = DataMonoFamily), color = MalakulTheme.colors.healthAccent)
                            }
                            Text("${lab.labProvidedRange} · ${lab.examDate}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddHealthRecordDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (diagnosis: String, hospitalName: String, doctorName: String, notes: String, recordDate: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var diagnosis by remember { mutableStateOf("") }
    var hospitalName by remember { mutableStateOf("") }
    var doctorName by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var recordDate by remember { mutableStateOf(todayIso) }
    var reminderDate by remember { mutableStateOf("") }
    var documentAttachment by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    fun showDatePicker(initialIso: String, onPicked: (String) -> Unit) {
        val cal = Calendar.getInstance()
        val parts = initialIso.split("-")
        if (parts.size == 3) {
            val y = parts[0].toIntOrNull()
            val m = parts[1].toIntOrNull()
            val d = parts[2].toIntOrNull()
            if (y != null && m != null && d != null) {
                cal.set(y, m - 1, d)
            }
        }
        android.app.DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val selectedCal = Calendar.getInstance()
                selectedCal.set(year, month, dayOfMonth)
                onPicked(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedCal.time))
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Health Record") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = diagnosis,
                    onValueChange = { diagnosis = it; error = null },
                    label = { Text("Diagnosis / Visit Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("health_diagnosis_input")
                )
                OutlinedTextField(
                    value = hospitalName,
                    onValueChange = { hospitalName = it; error = null },
                    label = { Text("Hospital / Clinic") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("health_hospital_input")
                )
                OutlinedTextField(
                    value = doctorName,
                    onValueChange = { doctorName = it; error = null },
                    label = { Text("Doctor Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("health_doctor_input")
                )
                OutlinedTextField(
                    value = recordDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Record Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker(recordDate) { recordDate = it; error = null } }) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Pilih Tanggal Periksa")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("health_date_input")
                        .clickable { showDatePicker(recordDate) { recordDate = it; error = null } }
                )
                OutlinedTextField(
                    value = reminderDate,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Pengingat Kontrol / Kalender (Opsional)") },
                    placeholder = { Text("Pilih tanggal jadwal kontrol berikutnya") },
                    trailingIcon = {
                        IconButton(onClick = {
                            val base = if (reminderDate.isNotBlank()) reminderDate else todayIso
                            showDatePicker(base) { reminderDate = it }
                        }) {
                            Icon(Icons.Outlined.EventNote, contentDescription = "Pilih Pengingat")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val base = if (reminderDate.isNotBlank()) reminderDate else todayIso
                            showDatePicker(base) { reminderDate = it }
                        }
                )
                OutlinedTextField(
                    value = documentAttachment,
                    onValueChange = { documentAttachment = it },
                    label = { Text("Lampiran Dokumen / File (Nama / Link)") },
                    placeholder = { Text("Contoh: Hasil-Lab-Darah.pdf / Resep-Obat.jpg") },
                    trailingIcon = {
                        Icon(Icons.Outlined.AttachFile, contentDescription = "Lampiran Dokumen")
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Recommendations") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("health_notes_input")
                )
                if (error != null) {
                    Text(error!!, style = MaterialTheme.typography.bodySmall, color = MalakulTheme.colors.danger)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSubmitting,
                onClick = {
                    if (diagnosis.isBlank() || hospitalName.isBlank() || doctorName.isBlank() || recordDate.isBlank()) {
                        error = "Please complete diagnosis, hospital, doctor, and date."
                    } else {
                        val fullNotesBuilder = StringBuilder(notes.trim())
                        if (reminderDate.isNotBlank()) {
                            if (fullNotesBuilder.isNotEmpty()) fullNotesBuilder.append("\n")
                            fullNotesBuilder.append("📅 Pengingat Kontrol: $reminderDate")
                        }
                        if (documentAttachment.isNotBlank()) {
                            if (fullNotesBuilder.isNotEmpty()) fullNotesBuilder.append("\n")
                            fullNotesBuilder.append("📎 Lampiran Dokumen: ${documentAttachment.trim()}")
                        }
                        onSubmit(diagnosis.trim(), hospitalName.trim(), doctorName.trim(), fullNotesBuilder.toString(), recordDate.trim())
                    }
                },
                modifier = Modifier.testTag("health_submit_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun HealthInfoLine(label: String, value: String) {
    Column {
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}
