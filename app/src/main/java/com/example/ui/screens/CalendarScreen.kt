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
import androidx.compose.runtime.Composable
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
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("UNIFIED SCHEDULE · MOCK", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.gold)
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
