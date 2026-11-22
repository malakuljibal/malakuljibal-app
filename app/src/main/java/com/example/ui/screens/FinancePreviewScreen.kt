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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.domain.model.AccessLevel
import com.example.domain.model.BudgetCategoryItem
import com.example.domain.model.TransactionItem
import com.example.domain.model.TransactionType
import com.example.domain.model.WalletItem
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
fun FinancePreviewScreen(
    state: MalakulUiState,
    onRefresh: () -> Unit,
    onDismissFeedback: () -> Unit,
    onAddTransaction: (type: String, amount: Double, description: String, transactionDate: String) -> Unit,
    onDeleteTransaction: (String) -> Unit,
    onSelectPrivacyFilter: (AccessLevel?) -> Unit,
    onSaveWallet: (id: String?, name: String, initialBalance: Double, iconKey: String, colorHex: String) -> Unit = { _, _, _, _, _ -> },
    onDeleteWallet: (String) -> Unit = {},
    onUpdateBudgetLimit: (String, Double) -> Unit = { _, _ -> },
    onUpdateBudgetCategoryIcon: (String, String) -> Unit = { _, _ -> },
    modifier: Modifier = Modifier
) {
    val activeUser = state.activeUser
    val canViewFinance = state.isLiveMode || AccessControl.canViewFinanceOverview(activeUser)
    var showAddDialog by remember { mutableStateOf(false) }
    var pendingDeleteTx by remember { mutableStateOf<TransactionItem?>(null) }

    // Wallet states
    var showWalletDialog by remember { mutableStateOf(false) }
    var editingWallet by remember { mutableStateOf<WalletItem?>(null) }
    var pendingDeleteWallet by remember { mutableStateOf<WalletItem?>(null) }

    // Budget category states
    var selectedBudgetCategoryId by remember(state.budgetCategories) {
        mutableStateOf(state.budgetCategories.firstOrNull()?.id ?: "")
    }
    var showBudgetLimitDialog by remember { mutableStateOf(false) }
    var showIconPickerDialog by remember { mutableStateOf(false) }
    var iconPickerTarget by remember { mutableStateOf<Pair<String, String>?>(null) } // (targetType: "wallet"|"category", id: String)

    if (showAddDialog) {
        AddTransactionDialog(
            isSubmitting = state.isSubmitting,
            wallets = state.wallets,
            budgetCategories = state.budgetCategories,
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

    if (showWalletDialog) {
        WalletEditDialog(
            wallet = editingWallet,
            onDismiss = {
                showWalletDialog = false
                editingWallet = null
            },
            onSave = { name, initialBal, iconKey, colorHex ->
                onSaveWallet(editingWallet?.id, name, initialBal, iconKey, colorHex)
                showWalletDialog = false
                editingWallet = null
            }
        )
    }

    pendingDeleteWallet?.let { wallet ->
        AlertDialog(
            onDismissRequest = { pendingDeleteWallet = null },
            icon = { Icon(Icons.Outlined.AccountBalanceWallet, contentDescription = null, tint = MalakulTheme.colors.danger) },
            title = { Text("Hapus Dompet?") },
            text = { Text("Hapus dompet \"${wallet.name}\"? Saldo dan pencatatan dompet ini akan ditiadakan dari daftar.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteWallet(wallet.id)
                        pendingDeleteWallet = null
                    }
                ) { Text("Hapus", color = MalakulTheme.colors.danger) }
            },
            dismissButton = {
                TextButton(onClick = { pendingDeleteWallet = null }) { Text("Batal") }
            }
        )
    }

    val selectedCategory = state.budgetCategories.find { it.id == selectedBudgetCategoryId }
        ?: state.budgetCategories.firstOrNull()

    if (showBudgetLimitDialog && selectedCategory != null) {
        BudgetLimitEditDialog(
            category = selectedCategory,
            currencySymbol = state.financialSummary.currencySymbol,
            onDismiss = { showBudgetLimitDialog = false },
            onSave = { newLimit ->
                onUpdateBudgetLimit(selectedCategory.id, newLimit)
                showBudgetLimitDialog = false
            }
        )
    }

    if (showIconPickerDialog && iconPickerTarget != null) {
        val target = iconPickerTarget!!
        IconPickerDialog(
            currentIconKey = when (target.first) {
                "wallet" -> state.wallets.find { it.id == target.second }?.iconKey ?: "account_balance_wallet"
                "category" -> state.budgetCategories.find { it.id == target.second }?.iconKey ?: "category"
                else -> "category"
            },
            onDismiss = {
                showIconPickerDialog = false
                iconPickerTarget = null
            },
            onSelectIcon = { newIconKey ->
                when (target.first) {
                    "wallet" -> {
                        val w = state.wallets.find { it.id == target.second }
                        if (w != null) {
                            onSaveWallet(w.id, w.name, w.initialBalance, newIconKey, w.colorHex)
                        }
                    }
                    "category" -> {
                        onUpdateBudgetCategoryIcon(target.second, newIconKey)
                    }
                }
                showIconPickerDialog = false
                iconPickerTarget = null
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

        // Section: Dompet & Rekening (Wallets)
        item {
            SectionHeader(
                title = "Dompet & Rekening",
                subtitle = "${state.wallets.size} dompet tersimpan",
                actionLabel = "+ Dompet",
                onActionClick = {
                    editingWallet = null
                    showWalletDialog = true
                }
            )
            Spacer(Modifier.height(8.dp))

            if (state.wallets.isEmpty()) {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AccountBalanceWallet,
                            contentDescription = null,
                            tint = MalakulTheme.colors.textMuted,
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Belum ada dompet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Tambahkan dompet/rekening baru dan sesuaikan saldo awalnya.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MalakulTheme.colors.textMuted
                        )
                        Spacer(Modifier.height(12.dp))
                        OutlinedButton(onClick = {
                            editingWallet = null
                            showWalletDialog = true
                        }) {
                            Text("+ Tambah Dompet")
                        }
                    }
                }
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(state.wallets.size) { idx ->
                        val wallet = state.wallets[idx]
                        val iconVector = getFinanceIcon(wallet.iconKey)
                        MalakulCard(
                            modifier = Modifier.width(220.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    IconButton(
                                        onClick = {
                                            iconPickerTarget = Pair("wallet", wallet.id)
                                            showIconPickerDialog = true
                                        },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MalakulTheme.colors.gold.copy(alpha = 0.15f),
                                            modifier = Modifier.fillMaxSize()
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = iconVector,
                                                    contentDescription = "Ganti Logo",
                                                    tint = MalakulTheme.colors.gold,
                                                    modifier = Modifier.size(20.dp)
                                                )
                                            }
                                        }
                                    }
                                    Row {
                                        IconButton(
                                            onClick = {
                                                editingWallet = wallet
                                                showWalletDialog = true
                                            },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.Edit,
                                                contentDescription = "Edit Dompet",
                                                tint = MalakulTheme.colors.textMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        IconButton(
                                            onClick = { pendingDeleteWallet = wallet },
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                Icons.Outlined.DeleteOutline,
                                                contentDescription = "Hapus Dompet",
                                                tint = MalakulTheme.colors.danger,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                                Spacer(Modifier.height(8.dp))
                                Text(
                                    text = wallet.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = "Saldo: ${formatCurrency(wallet.currentBalance, state.financialSummary.currencySymbol)}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MalakulTheme.colors.textPrimary
                                )
                                Text(
                                    text = "Saldo Awal: ${formatCurrency(wallet.initialBalance, state.financialSummary.currencySymbol)}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MalakulTheme.colors.textMuted
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section: Kategori Anggaran (Dropdown & Fitur Budgeting Kontrol Limit & Sisa Jatah)
        item {
            SectionHeader(
                title = "Kategori Anggaran & Budgeting",
                subtitle = "Kontrol limit dan sisa jatah per kategori"
            )
            Spacer(Modifier.height(8.dp))

            if (state.budgetCategories.isEmpty()) {
                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Tidak ada kategori anggaran aktif",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MalakulTheme.colors.textMuted
                        )
                    }
                }
            } else {
                var dropdownExpanded by remember { mutableStateOf(false) }
                val currentCategory = selectedCategory ?: state.budgetCategories.first()

                MalakulCard(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Pilih Kategori Anggaran",
                            style = MaterialTheme.typography.labelMedium,
                            color = MalakulTheme.colors.textMuted
                        )
                        Spacer(Modifier.height(6.dp))

                        // Dropdown Selector Button
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedCard(
                                onClick = { dropdownExpanded = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        Surface(
                                            shape = CircleShape,
                                            color = MalakulTheme.colors.gold.copy(alpha = 0.15f),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Icon(
                                                    imageVector = getFinanceIcon(currentCategory.iconKey),
                                                    contentDescription = null,
                                                    tint = MalakulTheme.colors.gold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = currentCategory.categoryName,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Icon(
                                        imageVector = Icons.Filled.ArrowDropDown,
                                        contentDescription = "Buka menu dropdown"
                                    )
                                }
                            }

                            DropdownMenu(
                                expanded = dropdownExpanded,
                                onDismissRequest = { dropdownExpanded = false },
                                modifier = Modifier.fillMaxWidth(0.9f)
                            ) {
                                state.budgetCategories.forEach { cat ->
                                    DropdownMenuItem(
                                        leadingIcon = {
                                            Icon(
                                                imageVector = getFinanceIcon(cat.iconKey),
                                                contentDescription = null,
                                                tint = MalakulTheme.colors.gold,
                                                modifier = Modifier.size(20.dp)
                                            )
                                        },
                                        text = {
                                            Text(
                                                text = cat.categoryName,
                                                fontWeight = if (cat.id == currentCategory.id) FontWeight.Bold else FontWeight.Normal
                                            )
                                        },
                                        onClick = {
                                            selectedBudgetCategoryId = cat.id
                                            dropdownExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(Modifier.height(16.dp))

                        // Budgeting details: Limit, Terpakai, Sisa Jatah
                        val remaining = (currentCategory.plannedAmount - currentCategory.spentAmount).coerceAtLeast(0.0)
                        val progress = if (currentCategory.plannedAmount > 0) {
                            (currentCategory.spentAmount / currentCategory.plannedAmount).toFloat().coerceIn(0f, 1f)
                        } else 0f
                        val isOver = currentCategory.spentAmount > currentCategory.plannedAmount

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Limit Budget",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MalakulTheme.colors.textMuted
                                )
                                Text(
                                    text = formatCurrency(currentCategory.plannedAmount, state.financialSummary.currencySymbol),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isOver) "Kelebihan Budget" else "Sisa Jatah",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isOver) MalakulTheme.colors.danger else MalakulTheme.colors.textMuted
                                )
                                Text(
                                    text = if (isOver) {
                                        "-${formatCurrency(currentCategory.spentAmount - currentCategory.plannedAmount, state.financialSummary.currencySymbol)}"
                                    } else {
                                        formatCurrency(remaining, state.financialSummary.currencySymbol)
                                    },
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isOver) MalakulTheme.colors.danger else MalakulTheme.colors.success
                                )
                            }
                        }

                        Spacer(Modifier.height(10.dp))

                        // Progress Bar
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(999.dp)),
                            color = when {
                                isOver -> MalakulTheme.colors.danger
                                progress > 0.8f -> MalakulTheme.colors.warning
                                else -> MalakulTheme.colors.gold
                            },
                            trackColor = MalakulTheme.colors.surfaceVariant
                        )

                        Spacer(Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Terpakai: ${formatCurrency(currentCategory.spentAmount, state.financialSummary.currencySymbol)}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MalakulTheme.colors.textMuted
                            )
                            Text(
                                text = "${(progress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isOver) MalakulTheme.colors.danger else MalakulTheme.colors.textPrimary
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        // Actions for category: Ubah Limit & Ubah Logo
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { showBudgetLimitDialog = true },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ubah Limit")
                            }
                            OutlinedButton(
                                onClick = {
                                    iconPickerTarget = Pair("category", currentCategory.id)
                                    showIconPickerDialog = true
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Outlined.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Ubah Logo")
                            }
                        }
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
    wallets: List<WalletItem>,
    budgetCategories: List<BudgetCategoryItem>,
    onDismiss: () -> Unit,
    onSubmit: (type: String, amount: Double, description: String, transactionDate: String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val todayIso = remember { SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()) }
    var selectedType by remember { mutableStateOf("expense") }
    var amountText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var dateText by remember { mutableStateOf(todayIso) }
    var validationError by remember { mutableStateOf<String?>(null) }

    // Dropdown options
    val defaultExpenseCategories = listOf(
        "Makanan & Minuman",
        "Belanja Bulanan",
        "Transportasi",
        "Tagihan & Utilitas",
        "Kesehatan & Obat",
        "Pendidikan",
        "Hiburan & Rekreasi",
        "Lain-lain"
    )
    val defaultIncomeCategories = listOf(
        "Gaji Pokok",
        "Bonus & Investasi",
        "Usaha & Bisnis",
        "Lain-lain"
    )

    val categoryList = remember(selectedType, budgetCategories) {
        val budgetNames = budgetCategories.map { it.name }
        if (selectedType == "expense") {
            (budgetNames + defaultExpenseCategories).distinct()
        } else {
            defaultIncomeCategories
        }
    }

    var selectedCategory by remember(categoryList) { mutableStateOf(categoryList.firstOrNull() ?: "Lain-lain") }
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    val walletOptions = remember(wallets) {
        if (wallets.isNotEmpty()) wallets.map { it.name } else listOf("Kas Utama", "Rekening Bank")
    }
    var selectedWallet by remember(walletOptions) { mutableStateOf(walletOptions.firstOrNull() ?: "Kas Utama") }
    var walletDropdownExpanded by remember { mutableStateOf(false) }

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
                validationError = null
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

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

                // Dropdown Pilihan Kategori
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { categoryDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Kategori", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.textMuted)
                                Text(selectedCategory, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Pilih Kategori")
                        }
                    }
                    DropdownMenu(
                        expanded = categoryDropdownExpanded,
                        onDismissRequest = { categoryDropdownExpanded = false }
                    ) {
                        categoryList.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    selectedCategory = cat
                                    categoryDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                // Dropdown Pilihan Dompet (Wallet)
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedCard(
                        onClick = { walletDropdownExpanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Dompet / Rekening", style = MaterialTheme.typography.labelSmall, color = MalakulTheme.colors.textMuted)
                                Text(selectedWallet, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            }
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Pilih Dompet")
                        }
                    }
                    DropdownMenu(
                        expanded = walletDropdownExpanded,
                        onDismissRequest = { walletDropdownExpanded = false }
                    ) {
                        walletOptions.forEach { wName ->
                            DropdownMenuItem(
                                text = { Text(wName) },
                                onClick = {
                                    selectedWallet = wName
                                    walletDropdownExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = amountText,
                    onValueChange = { amountText = it; validationError = null },
                    label = { Text("Amount (Rp)") },
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
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Transaction Date") },
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker() }) {
                            Icon(Icons.Outlined.CalendarToday, contentDescription = "Pilih Tanggal Transaksi")
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("tx_date_input")
                        .clickable { showDatePicker() }
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
                        val fullDescription = if (description.isNotBlank()) {
                            "[$selectedCategory] ${description.trim()} · $selectedWallet"
                        } else {
                            "[$selectedCategory] · $selectedWallet"
                        }
                        onSubmit(selectedType, amount, fullDescription, dateText.trim())
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

// Dialog: Tambah/Edit Dompet (Wallet)
@Composable
private fun WalletEditDialog(
    wallet: WalletItem?,
    onDismiss: () -> Unit,
    onSave: (name: String, initialBalance: Double, iconKey: String, colorHex: String) -> Unit
) {
    var name by remember { mutableStateOf(wallet?.name ?: "") }
    var initialBalanceText by remember { mutableStateOf(wallet?.initialBalance?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "0") }
    var iconKey by remember { mutableStateOf(wallet?.iconKey ?: "account_balance_wallet") }
    var colorHex by remember { mutableStateOf(wallet?.colorHex ?: "#D4AF37") }
    var showPicker by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (showPicker) {
        IconPickerDialog(
            currentIconKey = iconKey,
            onDismiss = { showPicker = false },
            onSelectIcon = {
                iconKey = it
                showPicker = false
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (wallet == null) "Tambah Dompet Baru" else "Edit Dompet") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it; errorMessage = null },
                    label = { Text("Nama Dompet / Rekening") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = initialBalanceText,
                    onValueChange = { initialBalanceText = it; errorMessage = null },
                    label = { Text("Saldo Awal (Rp)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                // Icon Selection Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = MalakulTheme.colors.gold.copy(alpha = 0.15f),
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = getFinanceIcon(iconKey),
                                    contentDescription = null,
                                    tint = MalakulTheme.colors.gold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                        Text("Logo/Ikon Dompet", style = MaterialTheme.typography.bodyMedium)
                    }
                    TextButton(onClick = { showPicker = true }) {
                        Text("Pilih Ikon")
                    }
                }

                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MalakulTheme.colors.danger,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isBlank()) {
                        errorMessage = "Nama dompet tidak boleh kosong"
                        return@Button
                    }
                    val bal = initialBalanceText.toDoubleOrNull()
                    if (bal == null || bal < 0) {
                        errorMessage = "Saldo awal harus berupa angka valid"
                        return@Button
                    }
                    onSave(name.trim(), bal, iconKey, colorHex)
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// Dialog: Edit Limit Anggaran Kategori
@Composable
private fun BudgetLimitEditDialog(
    category: BudgetCategoryModel,
    currencySymbol: String,
    onDismiss: () -> Unit,
    onSave: (newLimit: Double) -> Unit
) {
    var limitText by remember {
        mutableStateOf(
            if (category.plannedAmount % 1.0 == 0.0) category.plannedAmount.toLong().toString()
            else category.plannedAmount.toString()
        )
    }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Atur Limit: ${category.categoryName}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Tentukan batas pengeluaran bulanan untuk mengontrol sisa jatah budget kategori ini.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MalakulTheme.colors.textMuted
                )
                OutlinedTextField(
                    value = limitText,
                    onValueChange = { limitText = it; errorMessage = null },
                    label = { Text("Limit Anggaran ($currencySymbol)") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = MalakulTheme.colors.danger,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val parsed = limitText.toDoubleOrNull()
                    if (parsed == null || parsed < 0) {
                        errorMessage = "Masukkan nominal limit yang valid"
                        return@Button
                    }
                    onSave(parsed)
                }
            ) {
                Text("Simpan")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

// Dialog: Pemilihan Logo/Ikon
@Composable
private fun IconPickerDialog(
    currentIconKey: String,
    onDismiss: () -> Unit,
    onSelectIcon: (iconKey: String) -> Unit
) {
    val iconsList = listOf(
        Pair("account_balance_wallet", "Wallet"),
        Pair("credit_card", "Kartu"),
        Pair("savings", "Tabungan"),
        Pair("payments", "Kas/Tunai"),
        Pair("account_balance", "Bank"),
        Pair("shopping_cart", "Belanja"),
        Pair("restaurant", "Kuliner"),
        Pair("directions_car", "Transport"),
        Pair("home", "Rumah"),
        Pair("local_hospital", "Kesehatan"),
        Pair("school", "Pendidikan"),
        Pair("receipt_long", "Tagihan"),
        Pair("work", "Kantor"),
        Pair("fitness_center", "Kebugaran"),
        Pair("category", "Lainnya")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Pilih Logo / Ikon") },
        text = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 280.dp)
            ) {
                items(iconsList.size) { idx ->
                    val (key, label) = iconsList[idx]
                    val isSelected = key == currentIconKey
                    OutlinedCard(
                        onClick = { onSelectIcon(key) },
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) MalakulTheme.colors.gold.copy(alpha = 0.2f)
                            else Color.Transparent
                        ),
                        modifier = Modifier
                            .aspectRatio(1f)
                            .clip(RoundedCornerShape(8.dp))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = getFinanceIcon(key),
                                contentDescription = label,
                                tint = if (isSelected) MalakulTheme.colors.gold else MalakulTheme.colors.textPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Batal") }
        }
    )
}

private fun getFinanceIcon(key: String): ImageVector {
    return when (key) {
        "account_balance_wallet" -> Icons.Outlined.AccountBalanceWallet
        "credit_card" -> Icons.Outlined.CreditCard
        "savings" -> Icons.Outlined.Savings
        "payments" -> Icons.Outlined.Payments
        "account_balance" -> Icons.Outlined.AccountBalance
        "shopping_cart" -> Icons.Outlined.ShoppingCart
        "restaurant" -> Icons.Outlined.Restaurant
        "directions_car" -> Icons.Outlined.DirectionsCar
        "home" -> Icons.Outlined.Home
        "local_hospital" -> Icons.Outlined.LocalHospital
        "school" -> Icons.Outlined.School
        "receipt_long" -> Icons.Outlined.ReceiptLong
        "work" -> Icons.Outlined.Work
        "fitness_center" -> Icons.Outlined.FitnessCenter
        else -> Icons.Outlined.Category
    }
}
