package com.kashuapp.ui.budget

import com.kashuapp.data.budget.Budget
import com.kashuapp.data.category.Category

data class BudgetState(
    val budgets: List<Budget> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedMonth: Int = 1,
    val selectedYear: Int = 2026,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val totalBudgeted: Double = 0.0,
    val totalSpent: Double = 0.0,
    val overallProgressFraction: Float = 0.0f,
    val overallPercentage: Int = 0,

    val isFormOpen: Boolean = false,
    val isEditing: Boolean = false,
    val budgetToEdit: Budget? = null,
    val categoryId: String = "",
    val categoryName: String = "",
    val amountLimit: String = "",
    val isAmountError: String? = null,
    val isCategoryError: String? = null
)
