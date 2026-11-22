package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.R
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import androidx.compose.foundation.clickable
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChildDevelopmentPreviewScreen(
    state: MalakulUiState,
    onRefreshBabyGrowth: () -> Unit,
    onUpdateLiveBabyId: (String) -> Unit,
    onAddBabyGrowth: (babyId: String, weightKg: Double, lengthCm: Double, headCircumferenceCm: Double, measurementDate: String) -> Unit,
    onDismissFeedback: () -> Unit,
    onSelectChild: (String) -> Unit,
    onSelectGrowthMetric: (Int) -> Unit,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val child = state.selectedChildProfile
    val childMember = state.members.firstOrNull { it.id == child.memberId }
    val growthData = if (state.isLiveMode) state.growthMeasurements else state.growthMeasurements.filter { it.childMemberId == child.memberId }
    val milestones = state.milestones.filter { it.childMemberId == child.memberId }
    val feedingLogs = state.feedingLogs.filter { it.childMemberId == child.memberId }
    val immunizations = state.immunizations.filter { it.childMemberId == child.memberId }
    val journal = state.babyJournalEntries.filter { it.childMemberId == child.memberId }

    var showAddGrowthDialog by remember { mutableStateOf(false) }
    var babyIdInput by remember(state.liveBabyId) { mutableStateOf(state.liveBabyId) }

    if (showAddGrowthDialog) {
        AddBabyGrowthDialog(
            babyId = state.liveBabyId,
            isSubmitting = state.isSubmitting,
            onDismiss = { showAddGrowthDialog = false },
            onSubmit = { weight, length, head, date ->
                onAddBabyGrowth(state.liveBabyId, weight, length, head, date)
                showAddGrowthDialog = false
            }
        )
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (state.isLiveMode) "MODUL ANAK · AKTIF #${state.liveBabyId}" else "MODUL TUMBUH KEMBANG ANAK",
                            style = MaterialTheme.typography.labelSmall,
                            color = MalakulTheme.colors.gold
                        )
                        Text("Tumbuh Kembang & Kesehatan Anak", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("child_dev_back_button")
                            .size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isLiveMode) {
                        IconButton(
                            onClick = onRefreshBabyGrowth,
                            modifier = Modifier.testTag("baby_refresh_btn").size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh baby growth")
                        }
                        IconButton(
                            onClick = { showAddGrowthDialog = true },
                            modifier = Modifier.testTag("baby_add_growth_btn").size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = "Log baby growth measurement")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("child_dev_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (state.isLoadingBaby || state.isSubmitting) {
                item {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(999.dp)),
                        color = MalakulTheme.colors.gold
                    )
                }
            }

            if (state.globalError != null || state.actionMessage != null) {
                item {
                    ApiFeedbackBanner(
                        errorMessage = state.globalError,
                        successMessage = state.actionMessage,
                        onRetry = if (state.isLiveMode) onRefreshBabyGrowth else null,
                        onDismiss = onDismissFeedback
                    )
                }
            }

            // Live Baby ID Selector Bar (for live /baby/growth/{baby_id} endpoint)
            if (state.isLiveMode) {
                item {
                    MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Text("BABY PROFILE ID (/baby/growth/{baby_id})", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                        Spacer(Modifier.height(8.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = babyIdInput,
                                onValueChange = { babyIdInput = it },
                                label = { Text("Baby ID") },
                                singleLine = true,
                                modifier = Modifier.weight(1f).testTag("live_baby_id_input")
                            )
                            FilledTonalButton(
                                onClick = { onUpdateLiveBabyId(babyIdInput) },
                                modifier = Modifier.defaultMinSize(minHeight = 52.dp).testTag("load_baby_id_btn")
                            ) {
                                Text("Load")
                            }
                            Button(
                                onClick = { showAddGrowthDialog = true },
                                modifier = Modifier.defaultMinSize(minHeight = 52.dp)
                            ) {
                                Text("+ Log")
                            }
                        }
                    }
                }
            } else {
                // Offline Child Switcher
                item {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.childProfiles, key = { it.memberId }) { profile ->
                            val selected = profile.memberId == child.memberId
                            FilterChip(
                                selected = selected,
                                onClick = { onSelectChild(profile.memberId) },
                                label = { Text("${profile.fullName.substringBefore(" ")} (${profile.ageSummary.substringBefore(",")})") },
                                leadingIcon = {
                                    Icon(Icons.Outlined.ChildCare, contentDescription = null, modifier = Modifier.size(18.dp))
                                }
                            )
                        }
                    }
                }
            }

            // Pediatric Hero Card
            item {
                val latest = growthData.lastOrNull()
                MalakulCard(
                    containerColor = MalakulTheme.colors.navyHero,
                    contentPadding = PaddingValues(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        if (childMember != null) {
                            MemberAvatar(member = childMember, size = 60.dp, isSelected = true)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (state.isLiveMode) "Baby Profile #${state.liveBabyId}" else child.fullName,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MalakulTheme.colors.onNavyHero
                            )
                            Text(
                                text = if (latest != null) "Latest entry: ${latest.dateLabel}" else "No measurements recorded yet",
                                style = MaterialTheme.typography.bodySmall,
                                color = MalakulTheme.colors.gold
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        MetricStatBlock("Berat", "${latest?.weightKg ?: 0f} kg", MalakulTheme.colors.onNavyHero, modifier = Modifier.weight(1f))
                        MetricStatBlock("Panjang/Tinggi", "${latest?.lengthCm ?: 0f} cm", MalakulTheme.colors.onNavyHero, modifier = Modifier.weight(1f))
                        MetricStatBlock("Lingkar Kepala", "${latest?.headCircumferenceCm ?: 0f} cm", MalakulTheme.colors.onNavyHero, modifier = Modifier.weight(1f))
                    }
                }
            }

            // Growth Monitoring Chart + Honest WHO Note
            item {
                val metricTitles = listOf("Weight (kg)", "Length (cm)", "Head (cm)")
                val points = growthData.map { gm ->
                    val value = when (state.selectedGrowthMetric) {
                        0 -> gm.weightKg
                        1 -> gm.lengthCm
                        else -> gm.headCircumferenceCm
                    }
                    gm.dateLabel to value
                }

                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Text("Growth Monitoring Trends", style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        metricTitles.forEachIndexed { idx, label ->
                            FilterChip(
                                selected = state.selectedGrowthMetric == idx,
                                onClick = { onSelectGrowthMetric(idx) },
                                label = { Text(label) }
                            )
                        }
                    }
                    Spacer(Modifier.height(14.dp))
                    if (points.isEmpty()) {
                        Text(
                            text = "No growth measurements available for Baby #${state.liveBabyId}. Tap '+ Log' to record weight, length, and head circumference.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        LineTrendChart(
                            points = points,
                            lineColor = MalakulTheme.colors.gold,
                            unitSuffix = ""
                        )
                    }
                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        border = BorderStroke(1.dp, MalakulTheme.colors.cardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Icon(Icons.Outlined.Info, contentDescription = null, tint = MalakulTheme.colors.healthAccent, modifier = Modifier.size(16.dp))
                            Text(
                                text = "Kurva pertumbuhan merujuk standar WHO Child Growth Standards (berat/panjang/lingkar kepala). Tetap konsultasikan hasil pengukuran dengan dokter spesialis anak atau posyandu terdekat.",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Chronological Growth Measurements Log
            if (growthData.isNotEmpty()) {
                item {
                    SectionHeader(
                        title = "Recorded Measurements",
                        subtitle = "${growthData.size} measurement(s) synced"
                    )
                }
                items(growthData.reversed(), key = { it.id }) { gm ->
                    MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(gm.dateLabel, style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                            Text(gm.note, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(8.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            MetricStatBlock("Berat", "${gm.weightKg} kg", modifier = Modifier.weight(1f))
                            MetricStatBlock("Panjang", "${gm.lengthCm} cm", modifier = Modifier.weight(1f))
                            MetricStatBlock("Lingkar Kpl", "${gm.headCircumferenceCm} cm", modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            // Offline Demo Secondary Sections
            if (!state.isLiveMode) {
                item {
                    SectionHeader(title = "Developmental Milestones", subtitle = "5 pediatric development domains")
                }
                items(milestones, key = { it.id }) { ms ->
                    MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Text(ms.domain.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                        Text(ms.title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Text(ms.observation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                if (immunizations.isNotEmpty()) {
                    item { SectionHeader(title = "Immunization Schedule") }
                    items(immunizations, key = { it.id }) { imm ->
                        MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Text("${imm.vaccineName} · ${imm.doseLabel}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${imm.provider} · ${imm.dateLabel}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (feedingLogs.isNotEmpty()) {
                    item { SectionHeader(title = "Nutrition & MPASI Feeding") }
                    items(feedingLogs, key = { it.id }) { log ->
                        MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Text("${log.feedingType} · ${log.timeLabel}", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text(log.foodDetail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (journal.isNotEmpty()) {
                    item { SectionHeader(title = "Daily Baby Journal") }
                    items(journal, key = { it.id }) { entry ->
                        MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(entry.summary, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                                Text(entry.timeLabel, style = MaterialTheme.typography.labelSmall.copy(fontFamily = DataMonoFamily))
                            }
                            Text(entry.detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }

            item {
                Text(
                    text = stringResource(id = R.string.medical_disclaimer),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AddBabyGrowthDialog(
    babyId: String,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (weightKg: Double, lengthCm: Double, headCircumferenceCm: Double, measurementDate: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var weightText by remember { mutableStateOf("") }
    var lengthText by remember { mutableStateOf("") }
    var headText by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(todayIso) }
    var error by remember { mutableStateOf<String?>(null) }

    fun showDatePicker() {
        val cal = Calendar.getInstance()
        val parts = dateText.split("-")
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
                dateText = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selectedCal.time)
                error = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Growth · Baby #$babyId") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = weightText,
                    onValueChange = { weightText = it; error = null },
                    label = { Text("Weight (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("baby_weight_input")
                )
                OutlinedTextField(
                    value = lengthText,
                    onValueChange = { lengthText = it; error = null },
                    label = { Text("Length / Height (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("baby_length_input")
                )
                OutlinedTextField(
                    value = headText,
                    onValueChange = { headText = it; error = null },
                    label = { Text("Head Circumference (cm)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("baby_head_input")
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Measurement Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Pilih Tanggal")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("baby_date_input")
                        .clickable { showDatePicker() }
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
                    val w = weightText.trim().toDoubleOrNull()
                    val l = lengthText.trim().toDoubleOrNull()
                    val h = headText.trim().toDoubleOrNull()
                    if (w == null || l == null || h == null || w <= 0 || l <= 0 || h <= 0) {
                        error = "Please enter valid positive numbers for weight, length, and head circumference."
                    } else if (dateText.isBlank()) {
                        error = "Please enter a valid date (YYYY-MM-DD)."
                    } else {
                        onSubmit(w, l, h, dateText.trim())
                    }
                },
                modifier = Modifier.testTag("baby_submit_btn")
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
