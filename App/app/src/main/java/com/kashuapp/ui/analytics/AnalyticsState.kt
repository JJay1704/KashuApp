package com.kashuapp.ui.analytics

data class CategoryExpenseShare(
    val categoryId: String,
    val categoryName: String,
    val amount: Double,
    val percentage: Float,
    val sweepAngle: Float = 0f
)

data class AnalyticsState(
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2026,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val savingsRate: Int = 0,
    val categoryExpenses: List<CategoryExpenseShare> = emptyList(),
    val hasData: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
