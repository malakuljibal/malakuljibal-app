package com.example.domain.model

enum class TransactionType(val label: String) {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran")
}

data class FinancialSummary(
    val monthLabel: String,
    val currencySymbol: String,
    val monthlyIncome: Long,
    val monthlyExpenses: Long,
    val availableCash: Long,
    val totalSavings: Long,
    val monthlyBudgetLimit: Long
) {
    val netBalance: Long get() = monthlyIncome - monthlyExpenses
    val budgetUtilization: Float
        get() = if (monthlyBudgetLimit <= 0L) 0f else (monthlyExpenses.toFloat() / monthlyBudgetLimit.toFloat())

    // Pos Anggaran & Safe-to-Spend Helpers
    val safeToSpend: Long
        get() = (availableCash - totalSavings.coerceAtLeast(0L)).coerceAtLeast(0L)

    val isBudgetWarning: Boolean
        get() = budgetUtilization >= 0.80f && budgetUtilization < 1.0f

    val isBudgetExceeded: Boolean
        get() = budgetUtilization >= 1.0f
}

data class MonthlyComparison(
    val monthShort: String,
    val income: Long,
    val expense: Long
)

data class TransactionItem(
    val id: String,
    val title: String,
    val category: String,
    val amount: Long,
    val type: TransactionType,
    val date: String,
    val paymentMethod: String,
    val responsibleMemberId: String,
    val responsibleMemberName: String,
    val description: String,
    val hasReceipt: Boolean,
    val accessLevel: AccessLevel
)

data class BudgetCategoryItem(
    val id: String,
    val category: String,
    val plannedAmount: Long,
    val actualAmount: Long
) {
    val remaining: Long get() = plannedAmount - actualAmount
    val progress: Float
        get() = if (plannedAmount <= 0L) 0f else (actualAmount.toFloat() / plannedAmount.toFloat())
    val isOverspent: Boolean get() = actualAmount > plannedAmount
    val isWarning: Boolean get() = progress >= 0.80f && !isOverspent
}

data class SavingsGoal(
    val id: String,
    val title: String,
    val subtitle: String,
    val targetAmount: Long,
    val currentAmount: Long,
    val targetYear: String,
    val accessLevel: AccessLevel
) {
    val progress: Float
        get() = if (targetAmount <= 0L) 0f else (currentAmount.toFloat() / targetAmount.toFloat()).coerceIn(0f, 1f)
}
