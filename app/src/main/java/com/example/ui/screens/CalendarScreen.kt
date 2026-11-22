package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.domain.model.AccessLevel
import com.example.domain.model.EventCategory
import com.example.domain.model.FamilyEvent
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
    onUpdateEvent: (eventId: String, title: String, category: EventCategory, dateLabel: String, timeLabel: String, locationOrNote: String, assignedScope: String) -> Unit = { _, _, _, _, _, _, _ -> },
    onNavigateBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var editingEvent by remember { mutableStateOf<FamilyEvent?>(null) }

    if (editingEvent != null) {
        EditEventDialog(
            event = editingEvent!!,
            onDismiss = { editingEvent = null },
            onConfirm = { eventId, title, category, dateLabel, timeLabel, locationOrNote, assignedScope ->
                onUpdateEvent(eventId, title, category, dateLabel, timeLabel, locationOrNote, assignedScope)
                editingEvent = null
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
                    if (onNavigateBack != null) {
                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier
                                .testTag("calendar_back_button")
                                .size(48.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
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
                .testTag("calendar_events_list"),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 28.dp),
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
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(event.category.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
                                    IconButton(
                                        onClick = { editingEvent = event },
                                        modifier = Modifier.size(28.dp).testTag("edit_event_${event.id}")
                                    ) {
                                        Icon(
                                            Icons.Rounded.Edit,
                                            contentDescription = "Edit Event",
                                            tint = MalakulTheme.colors.gold,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
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
private fun EditEventDialog(
    event: FamilyEvent,
    onDismiss: () -> Unit,
    onConfirm: (
        eventId: String,
        title: String,
        category: EventCategory,
        dateLabel: String,
        timeLabel: String,
        locationOrNote: String,
        assignedScope: String
    ) -> Unit
) {
    var title by remember { mutableStateOf(event.title) }
    var category by remember { mutableStateOf(event.category) }
    var dateLabel by remember { mutableStateOf(event.dateLabel) }
    var timeLabel by remember { mutableStateOf(event.timeLabel) }
    var locationOrNote by remember { mutableStateOf(event.locationOrNote) }
    var assignedScope by remember { mutableStateOf(event.assignedScope) }
    var error by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Edit Jadwal / Event") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Judul Agenda") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_event_title")
                )

                Text("Kategori", style = MaterialTheme.typography.labelMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(EventCategory.entries) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.label) }
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dateLabel,
                        onValueChange = { dateLabel = it; error = null },
                        label = { Text("Tanggal (cth: 28 Nov 2026)") },
                        singleLine = true,
                        modifier = Modifier.weight(1.2f).testTag("edit_event_date")
                    )
                    OutlinedTextField(
                        value = timeLabel,
                        onValueChange = { timeLabel = it; error = null },
                        label = { Text("Waktu (cth: 10:30)") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("edit_event_time")
                    )
                }

                OutlinedTextField(
                    value = locationOrNote,
                    onValueChange = { locationOrNote = it },
                    label = { Text("Lokasi / Catatan") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_event_location")
                )

                OutlinedTextField(
                    value = assignedScope,
                    onValueChange = { assignedScope = it },
                    label = { Text("Penugasan / Scope") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("edit_event_scope")
                )

                if (error != null) {
                    Text(error!!, style = MaterialTheme.typography.bodySmall, color = MalakulTheme.colors.danger)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank() || dateLabel.isBlank()) {
                        error = "Judul dan tanggal wajib diisi."
                    } else {
                        onConfirm(
                            event.id,
                            title.trim(),
                            category,
                            dateLabel.trim(),
                            timeLabel.trim().ifBlank { "All Day" },
                            locationOrNote.trim(),
                            assignedScope.trim().ifBlank { "Family" }
                        )
                    }
                },
                modifier = Modifier.testTag("edit_event_confirm_btn")
            ) {
                Text("Simpan Perubahan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal")
            }
        }
    )
}
