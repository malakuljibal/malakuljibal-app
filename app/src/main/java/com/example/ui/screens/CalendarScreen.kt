package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material.icons.rounded.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.EventCategory
import com.example.ui.components.MalakulCard
import com.example.ui.components.PrivacyBadge
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    state: MalakulUiState,
    onSelectCategory: (EventCategory?) -> Unit,
    onNavigateBack: () -> Unit,
    onAddEvent: (title: String, category: EventCategory, dateLabel: String, timeLabel: String, locationOrNote: String, assignedScope: String) -> Unit = { _, _, _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var showAddEventDialog by remember { mutableStateOf(false) }

    if (showAddEventDialog) {
        AddEventDialog(
            onDismiss = { showAddEventDialog = false },
            onSubmit = { title, cat, date, time, loc, scope ->
                onAddEvent(title, cat, date, time, loc, scope)
                showAddEventDialog = false
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
                        Text("JADWAL KELUARGA TERPADU", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                        Text("Family Calendar & Reminders", style = MaterialTheme.typography.titleLarge)
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .testTag("calendar_back_button")
                            .size(48.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showAddEventDialog = true },
                        modifier = Modifier.padding(end = 8.dp).testTag("calendar_add_event_btn")
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Tambah")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddEventDialog = true },
                containerColor = MalakulTheme.colors.gold,
                contentColor = MalakulTheme.colors.navyHero,
                modifier = Modifier.testTag("calendar_fab_add")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Rounded.Add, contentDescription = "Tambah Jadwal")
                    Text("Jadwal Baru", fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .testTag("calendar_events_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    item {
                        FilterChip(
                            selected = state.selectedCalendarCategory == null,
                            onClick = { onSelectCategory(null) },
                            label = { Text("All Authorized") }
                        )
                    }
                    items(EventCategory.entries) { cat ->
                        FilterChip(
                            selected = state.selectedCalendarCategory == cat,
                            onClick = {
                                onSelectCategory(if (state.selectedCalendarCategory == cat) null else cat)
                            },
                            label = { Text(cat.label) }
                        )
                    }
                }
            }

            if (state.visibleEvents.isEmpty()) {
                item {
                    MalakulCard(modifier = Modifier.fillMaxWidth()) {
                        Text("No events in this category", style = MaterialTheme.typography.titleMedium)
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Only events assigned to or permitted for ${state.activeUser.shortName} are shown.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                items(state.visibleEvents, key = { it.id }) { event ->
                    MalakulCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MalakulTheme.colors.navyHero,
                                modifier = Modifier.width(62.dp)
                            ) {
                                val parts = event.dateLabel.split(" ")
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = parts.getOrNull(0) ?: "",
                                        style = MaterialTheme.typography.titleLarge.copy(fontFamily = DataMonoFamily),
                                        color = MalakulTheme.colors.gold
                                    )
                                    Text(
                                        text = (parts.getOrNull(1) ?: "").uppercase(),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MalakulTheme.colors.onNavyHero
                                    )
                                }
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(event.category.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                                Text(event.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Spacer(Modifier.height(2.dp))
                                Text("${event.timeLabel} · ${event.locationOrNote}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(8.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    PrivacyBadge(accessLevel = event.accessLevel)
                                    Text("Scope: ${event.assignedScope}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AddEventDialog(
    onDismiss: () -> Unit,
    onSubmit: (title: String, category: EventCategory, dateLabel: String, timeLabel: String, locationOrNote: String, assignedScope: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(EventCategory.FAMILY) }
    var dateLabel by remember { mutableStateOf("25 Nov") }
    var timeLabel by remember { mutableStateOf("10:00 WIB") }
    var locationOrNote by remember { mutableStateOf("") }
    var assignedScope by remember { mutableStateOf("Seluruh Keluarga") }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tambah Agenda & Pengingat Keluarga", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Nama Kegiatan / Pengingat") },
                    placeholder = { Text("contoh: Les Musik Ahmad / Bayar Listrik") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_title_input")
                )

                Text("Kategori:", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(EventCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategory == cat,
                            onClick = { selectedCategory = cat },
                            label = { Text(cat.label, style = MaterialTheme.typography.labelSmall) }
                        )
                    }
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateLabel,
                        onValueChange = { dateLabel = it; error = null },
                        label = { Text("Tanggal") },
                        placeholder = { Text("25 Nov") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("event_date_input")
                    )
                    OutlinedTextField(
                        value = timeLabel,
                        onValueChange = { timeLabel = it; error = null },
                        label = { Text("Waktu") },
                        placeholder = { Text("15:00 WIB") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("event_time_input")
                    )
                }

                OutlinedTextField(
                    value = locationOrNote,
                    onValueChange = { locationOrNote = it },
                    label = { Text("Lokasi / Catatan") },
                    placeholder = { Text("contoh: RS Bunda / Rumah") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_note_input")
                )

                OutlinedTextField(
                    value = assignedScope,
                    onValueChange = { assignedScope = it },
                    label = { Text("Peserta / Yang Terlibat") },
                    placeholder = { Text("Seluruh Keluarga / Ahmad") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("event_scope_input")
                )

                if (error != null) {
                    Text(error!!, style = MaterialTheme.typography.bodySmall, color = MalakulTheme.colors.danger)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) {
                        error = "Masukkan nama kegiatan."
                    } else if (dateLabel.isBlank()) {
                        error = "Masukkan tanggal kegiatan."
                    } else {
                        onSubmit(
                            title.trim(),
                            selectedCategory,
                            dateLabel.trim(),
                            timeLabel.trim().ifBlank { "Sepanjang Hari" },
                            locationOrNote.trim().ifBlank { "Rumah" },
                            assignedScope.trim().ifBlank { "Seluruh Keluarga" }
                        )
                    }
                },
                modifier = Modifier.testTag("event_submit_btn")
            ) {
                Text("Simpan Agenda")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}
