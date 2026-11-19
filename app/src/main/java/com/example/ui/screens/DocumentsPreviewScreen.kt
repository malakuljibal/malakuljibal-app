package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.domain.model.DocumentCategory
import com.example.domain.model.DocumentStatus
import com.example.domain.model.FamilyDocument
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentsPreviewScreen(
    state: MalakulUiState,
    onRefresh: () -> Unit,
    onDismissFeedback: () -> Unit,
    onAddDocument: (title: String, category: String, documentDate: String, notes: String) -> Unit,
    onDeleteDocument: (String) -> Unit,
    onSearchChange: (String) -> Unit,
    onSelectCategory: (DocumentCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    var inspectedDoc by remember { mutableStateOf<FamilyDocument?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingDeleteDoc by remember { mutableStateOf<FamilyDocument?>(null) }

    if (showAddDialog) {
        AddDocumentDialog(
            isSubmitting = state.isSubmitting,
            onDismiss = { showAddDialog = false },
            onSubmit = { title, category, docDate, notes ->
                onAddDocument(title, category, docDate, notes)
                showAddDialog = false
            }
        )
    }

    pendingDeleteDoc?.let { doc ->
        AlertDialog(
            onDismissRequest = { pendingDeleteDoc = null },
            icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = MalakulTheme.colors.danger) },
            title = { Text("Delete Document?") },
            text = { Text("Permanently remove \"${doc.title}\" from the family vault?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteDocument(doc.id)
                        pendingDeleteDoc = null
                    }
                ) { Text("Delete", color = MalakulTheme.colors.danger) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteDoc = null }) { Text("Cancel") }
            }
        )
    }

    inspectedDoc?.let { doc ->
        ModalBottomSheet(
            onDismissRequest = { inspectedDoc = null },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            DocumentDetailSheetContent(
                doc = doc,
                canDelete = state.isLiveMode,
                onDelete = {
                    inspectedDoc = null
                    pendingDeleteDoc = doc
                },
                onClose = { inspectedDoc = null }
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("documents_preview_list"),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (state.isLiveMode) "MODULE E · LIVE API" else "MODULE E · OFFLINE DEMO",
                        style = MaterialTheme.typography.labelSmall,
                        color = MalakulTheme.colors.gold
                    )
                    Text(
                        text = "Document Vault",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (state.isLiveMode) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .testTag("documents_refresh_btn")
                                .size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh documents")
                        }
                        FilledTonalButton(
                            onClick = { showAddDialog = true },
                            enabled = !state.isSubmitting,
                            modifier = Modifier
                                .testTag("vault_add_btn")
                                .defaultMinSize(minHeight = 48.dp)
                        ) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Add")
                        }
                    }
                }
            }
        }

        if (state.isLoadingDocuments || state.isSubmitting) {
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
                    onRetry = if (state.isLiveMode) onRefresh else null,
                    onDismiss = onDismissFeedback
                )
            }
        }

        // Security Header
        item {
            MalakulCard(
                containerColor = MalakulTheme.colors.navyHero,
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VerifiedUser,
                        contentDescription = "Private encrypted vault",
                        tint = MalakulTheme.colors.gold,
                        modifier = Modifier.size(26.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Private Household Document Custody",
                            style = MaterialTheme.typography.titleSmall,
                            color = MalakulTheme.colors.onNavyHero,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${state.visibleDocuments.size} accessible record(s) · Bearer JWT protected",
                            style = MaterialTheme.typography.bodySmall,
                            color = MalakulTheme.colors.onNavyHero.copy(alpha = 0.78f)
                        )
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = state.documentSearchQuery,
                onValueChange = onSearchChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("document_search_input"),
                placeholder = { Text("Search title, notes, or category...") },
                leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                trailingIcon = {
                    if (state.documentSearchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchChange("") }) {
                            Icon(Icons.Rounded.Close, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        // Category Filter Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DocumentCategory.entries) { cat ->
                    FilterChip(
                        selected = state.selectedDocumentCategory == cat,
                        onClick = { onSelectCategory(cat) },
                        label = { Text(cat.displayName) }
                    )
                }
            }
        }

        if (state.visibleDocuments.isEmpty()) {
            item {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.FolderOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No documents found", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (state.isLiveMode) "Tap '+ Add' to register a family document."
                            else "Adjust your search filter or category.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(state.visibleDocuments, key = { it.id }) { doc ->
                MalakulCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("document_card_${doc.id}"),
                    onClick = { inspectedDoc = doc },
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    if (doc.status == DocumentStatus.EXPIRING_SOON)
                                        MalakulTheme.colors.warningContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = doc.fileFormat.take(4),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (doc.status == DocumentStatus.EXPIRING_SOON)
                                    MalakulTheme.colors.warning
                                else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = doc.category.displayName.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MalakulTheme.colors.gold
                                )
                                Text(
                                    text = doc.issueDate,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(3.dp))
                            Text(
                                text = doc.title,
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = doc.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PrivacyBadge(accessLevel = doc.accessLevel)
                                if (state.isLiveMode) {
                                    IconButton(
                                        onClick = { pendingDeleteDoc = doc },
                                        modifier = Modifier
                                            .testTag("delete_doc_${doc.id}")
                                            .size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = "Delete document",
                                            tint = MalakulTheme.colors.danger,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
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
private fun AddDocumentDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (title: String, category: String, documentDate: String, notes: String) -> Unit
) {
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var title by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Identity") }
    var documentDate by remember { mutableStateOf(todayIso) }
    var notes by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }

    val quickCategories = listOf("Identity", "Passport", "Birth Certificate", "Insurance", "Property", "Medical", "Other")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Family Document") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it; error = null },
                    label = { Text("Document Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("doc_title_input")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(quickCategories) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat) }
                        )
                    }
                }
                OutlinedTextField(
                    value = documentDate,
                    onValueChange = { documentDate = it; error = null },
                    label = { Text("Document Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("doc_date_input")
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes / Metadata") },
                    minLines = 2,
                    modifier = Modifier.fillMaxWidth().testTag("doc_notes_input")
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
                    if (title.isBlank() || documentDate.isBlank()) {
                        error = "Please enter a title and document date."
                    } else {
                        onSubmit(title.trim(), category.trim(), documentDate.trim(), notes.trim())
                    }
                },
                modifier = Modifier.testTag("doc_submit_btn")
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
private fun DocumentDetailSheetContent(
    doc: FamilyDocument,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onClose: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            PrivacyBadge(accessLevel = doc.accessLevel)
            Text(
                text = "${doc.fileFormat} · ${doc.versionLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(text = doc.title, style = MaterialTheme.typography.headlineSmall)
        Text(
            text = doc.description,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        HorizontalDivider(color = MalakulTheme.colors.cardBorder)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            MetricStatBlock("Owner", doc.ownerName, modifier = Modifier.weight(1f))
            MetricStatBlock("Date", doc.issueDate, modifier = Modifier.weight(1f))
            MetricStatBlock("Category", doc.category.displayName, modifier = Modifier.weight(1f))
        }
        doc.recentAuditEntries.forEach { entry ->
            Text(
                text = "• $entry",
                style = MaterialTheme.typography.bodySmall.copy(fontFamily = DataMonoFamily),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (canDelete) {
                OutlinedButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .weight(1f)
                        .defaultMinSize(minHeight = 48.dp)
                ) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = MalakulTheme.colors.danger, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Delete", color = MalakulTheme.colors.danger)
                }
            }
            Button(
                onClick = onClose,
                modifier = Modifier
                    .weight(1f)
                    .defaultMinSize(minHeight = 48.dp)
            ) {
                Text("Close")
            }
        }
    }
}
