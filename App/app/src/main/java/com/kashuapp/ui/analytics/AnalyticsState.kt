package com.kashuapp.ui.analytics

import com.kashuapp.data.category.Category

data class AnalyticsState(
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2026,
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val netSavings: Double = 0.0,
    val savingsRate: Int = 0,
    val categoryExpenses: Map<Category, Double> = emptyMap(),
    val hasData: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
