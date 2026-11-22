package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.AccessLevel
import com.example.domain.model.TransactionItem
import com.example.domain.model.TransactionType
import com.example.domain.rbac.AccessControl
import com.example.ui.components.*
import com.example.ui.state.MalakulUiState
import com.example.ui.theme.DataMonoFamily
import com.example.ui.theme.MalakulTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun FinancePreviewScreen(
    state: MalakulUiState,
    onRefresh: () -> Unit,
    onDismissFeedback: () -> Unit,
    onAddTransaction: (type: String, amount: Double, description: String, transactionDate: String) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onSelectPrivacyFilter: (AccessLevel?) -> Unit,
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    val canViewFinance = state.isLiveMode || AccessControl.canViewFinanceOverview(activeUser)
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingDeleteTx by remember { mutableStateOf<TransactionItem?>(null) }

    if (showAddDialog) {
        AddTransactionDialog(
            isSubmitting = state.isSubmitting,
            onDismiss = { showAddDialog = false },
            onSubmit = { type, amount, desc, date ->
                onAddTransaction(type, amount, desc, date)
                showAddDialog = false
            }
        )
    }

    pendingDeleteTx?.let { tx ->
        AlertDialog(
            onDismissRequest = { pendingDeleteTx = null },
            icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = MalakulTheme.colors.danger) },
            title = { Text("Delete Transaction?") },
            text = { Text("Remove \"${tx.title}\" (${formatCurrency(tx.amount, state.financialSummary.currencySymbol)}) from the household ledger?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTransaction(tx.id)
                        pendingDeleteTx = null
                    }
                ) { Text("Delete", color = MalakulTheme.colors.danger) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteTx = null }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("finance_preview_list"),
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
                        text = if (state.isLiveMode) "MODUL KEUANGAN · AKTIF" else "MODUL KEUANGAN · KELUARGA SAMAWA",
                        style = MaterialTheme.typography.labelSmall,
                        color = MalakulTheme.colors.gold
                    )
                    Text(
                        text = "Keuangan Keluarga",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (state.isLiveMode) {
                        IconButton(
                            onClick = onRefresh,
                            modifier = Modifier
                                .testTag("finance_refresh_btn")
                                .size(48.dp)
                        ) {
                            Icon(Icons.Rounded.Refresh, contentDescription = "Refresh finances")
                        }
                        FilledTonalButton(
                            onClick = { showAddDialog = true },
                            enabled = !state.isSubmitting,
                            modifier = Modifier
                                .testTag("finance_add_btn")
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

        if (state.isLoadingFinance || state.isSubmitting) {
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

        if (!canViewFinance) {
            item {
                RestrictedAccessCard(
                    title = "Household Ledger Restricted",
                    reason = "Signed in as ${activeUser.fullName} (${activeUser.role.displayName}). Household income, budgets, and shared accounts are restricted to Super Admin and authorized Parents."
                )
            }
            return@LazyColumn
        }

        // Summary Navy Hero Card
        item {
            val fin = state.financialSummary
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.extraLarge,
                color = MalakulTheme.colors.navyHero,
                border = BorderStroke(1.dp, MalakulTheme.colors.gold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (state.isLiveMode) "LIVE SUMMARY · FAMILY #${state.authSession?.familyId ?: ""}"
                            else "${fin.monthLabel.uppercase()} · DEMO",
                            style = MaterialTheme.typography.labelSmall,
                            color = MalakulTheme.colors.gold
                        )
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = Color.White.copy(alpha = 0.12f)
                        ) {
                            val sign = if (fin.netBalance >= 0) "+" else ""
                            Text(
                                text = "Net $sign${formatCurrency(fin.netBalance, fin.currencySymbol)}",
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MalakulTheme.colors.gold
                            )
                        }
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = formatCurrency(fin.availableCash + fin.totalSavings, fin.currencySymbol),
                        style = MaterialTheme.typography.displayMedium.copy(fontFamily = DataMonoFamily),
                        color = MalakulTheme.colors.onNavyHero
                    )
                    Text(
                        text = "Household Balance & Reserves",
                        style = MaterialTheme.typography.bodySmall,
                        color = MalakulTheme.colors.onNavyHero.copy(alpha = 0.76f)
                    )
                    Spacer(Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pemasukan", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                            Text(formatCurrency(fin.monthlyIncome, fin.currencySymbol), style = MaterialTheme.typography.titleMedium.copy(fontFamily = DataMonoFamily), color = Color.White)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Pengeluaran", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                            Text(formatCurrency(fin.monthlyExpenses, fin.currencySymbol), style = MaterialTheme.typography.titleMedium.copy(fontFamily = DataMonoFamily), color = MalakulTheme.colors.gold)
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Sisa Kas", style = MaterialTheme.typography.labelMedium, color = Color.White.copy(alpha = 0.7f))
                            Text(formatCurrency(fin.availableCash, fin.currencySymbol), style = MaterialTheme.typography.titleMedium.copy(fontFamily = DataMonoFamily), color = Color.White)
                        }
                    }
                    if (fin.monthlyBudgetLimit > 0L) {
                        Spacer(Modifier.height(14.dp))
                        val utilPercent = (fin.budgetUtilization * 100).toInt()
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Batas Anggaran Bulanan (${utilPercent}%)",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            if (fin.isBudgetExceeded) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MalakulTheme.colors.danger
                                ) {
                                    Text(
                                        text = "OVER BUDGET",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (fin.isBudgetWarning) {
                                Surface(
                                    shape = RoundedCornerShape(999.dp),
                                    color = MalakulTheme.colors.warningContainer
                                ) {
                                    Text(
                                        text = "CAP ALERT 80%",
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MalakulTheme.colors.warning,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { fin.budgetUtilization.coerceIn(0f, 1f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(999.dp)),
                            color = when {
                                fin.isBudgetExceeded -> MalakulTheme.colors.danger
                                fin.isBudgetWarning -> MalakulTheme.colors.warning
                                else -> MalakulTheme.colors.gold
                            },
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )
                    }
                }
            }
        }

        // Transactions Section (Live Bound with Delete)
        item {
            SectionHeader(
                title = "Transactions",
                subtitle = if (state.isLiveMode) "${state.visibleTransactions.size} live records from server"
                else "Filtered by privacy scope for ${activeUser.shortName}",
                actionLabel = if (state.isLiveMode) "+ Record" else null,
                onActionClick = if (state.isLiveMode) ({ showAddDialog = true }) else null
            )
            Spacer(Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedFinanceFilter == null,
                        onClick = { onSelectPrivacyFilter(null) },
                        label = { Text("All Visible") }
                    )
                }
                items(AccessLevel.entries) { level ->
                    FilterChip(
                        selected = state.selectedFinanceFilter == level,
                        onClick = {
                            onSelectPrivacyFilter(if (state.selectedFinanceFilter == level) null else level)
                        },
                        label = { Text(level.label) }
                    )
                }
            }
        }

        if (state.visibleTransactions.isEmpty()) {
            item {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.ReceiptLong,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(34.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("No transactions recorded yet", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (state.isLiveMode) "Tap '+ Add' to record your first income or expense."
                            else "Try clearing the active privacy filter.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(state.visibleTransactions, key = { it.id }) { tx ->
                val isIncome = tx.type == TransactionType.INCOME
                MalakulCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    if (isIncome) MalakulTheme.colors.successContainer
                                    else MaterialTheme.colorScheme.surfaceVariant
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isIncome) Icons.Rounded.ArrowDownward else Icons.Rounded.ArrowUpward,
                                contentDescription = tx.type.label,
                                tint = if (isIncome) MalakulTheme.colors.success else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = tx.title,
                                    style = MaterialTheme.typography.titleSmall,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.weight(1f)
                                )
                                Text(
                                    text = "${if (isIncome) "+" else "-"}${formatCurrency(tx.amount, state.financialSummary.currencySymbol)}",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontFamily = DataMonoFamily,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    color = if (isIncome) MalakulTheme.colors.success else MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "${tx.category} · ${tx.date} · ${tx.responsibleMemberName}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(Modifier.height(6.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PrivacyBadge(accessLevel = tx.accessLevel)
                                if (state.isLiveMode) {
                                    IconButton(
                                        onClick = { pendingDeleteTx = tx },
                                        modifier = Modifier
                                            .testTag("delete_tx_${tx.id}")
                                            .size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Outlined.DeleteOutline,
                                            contentDescription = "Delete transaction",
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

        // Historical Chart & Budgets (Supporting Overview)
        item {
            MalakulCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Income vs. Expenses Trend", style = MaterialTheme.typography.titleMedium)
                        Text("6-month reference overview", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ChartLegendDot("Income", MalakulTheme.colors.navyHero)
                        ChartLegendDot("Expense", MalakulTheme.colors.gold)
                    }
                }
                Spacer(Modifier.height(16.dp))
                IncomeExpenseBarChart(data = state.monthlyComparisons)
            }
        }
    }
}

@Composable
private fun AddTransactionDialog(
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (type: String, amount: Double, description: String, transactionDate: String) -> Unit
) {
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var selectedType by remember { mutableStateOf("expense") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(todayIso) }
    var validationError by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record Transaction") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedType == "expense",
                        onClick = { selectedType = "expense" },
                        label = { Text("Expense") }
                    )
                    FilterChip(
                        selected = selectedType == "income",
                        onClick = { selectedType = "income" },
                        label = { Text("Income") }
                    )
                }
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; validationError = null },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_amount_input")
                )
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it; validationError = null },
                    label = { Text("Description") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_desc_input")
                )
                OutlinedTextField(
                    value = dateText,
                    onValueChange = { dateText = it; validationError = null },
                    label = { Text("Transaction Date (YYYY-MM-DD)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("tx_date_input")
                )
                if (validationError != null) {
                    Text(validationError!!, style = MaterialTheme.typography.bodySmall, color = MalakulTheme.colors.danger)
                }
            }
        },
        confirmButton = {
            Button(
                enabled = !isSubmitting,
                onClick = {
                    val amount = amountText.trim().toDoubleOrNull()
                    if (amount == null || amount <= 0.0) {
                        validationError = "Please enter a valid positive amount."
                    } else if (description.isBlank()) {
                        validationError = "Please enter a description."
                    } else if (dateText.isBlank()) {
                        validationError = "Please enter a date (YYYY-MM-DD)."
                    } else {
                        onSubmit(selectedType, amount, description.trim(), dateText.trim())
                    }
                },
                modifier = Modifier.testTag("tx_submit_btn")
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
private fun ChartLegendDot(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
