package com.kashuapp.goals.ui

import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category
import com.kashuapp.goals.data.Goal

/**
 * Estado general inmutable para la pantalla de Metas de Ahorro.
 * Modela visualización, operaciones modales y métricas consolidadas.
 */
data class GoalsState(
    val goals: List<Goal> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,

    // Control de diálogos modales
    val isFormOpen: Boolean = false,
    val goalToEdit: Goal? = null,
    val isAddSavingsOpen: Boolean = false,
    val selectedGoalForSavings: Goal? = null
) {
    val totalTargetAmount: Double
        get() = goals.sumOf { it.targetAmount }

    val totalSavedAmount: Double
        get() = goals.sumOf { it.currentAmount }

    val overallProgressFraction: Float
        get() = if (totalTargetAmount > 0.0) {
            (totalSavedAmount / totalTargetAmount).toFloat().coerceIn(0.0f, 1.0f)
        } else {
            0.0f
        }

    val overallProgressPercentage: Int
        get() = (overallProgressFraction * 100).toInt()

    val activeGoalsCount: Int
        get() = goals.count { !it.isTargetReached }

    val completedGoalsCount: Int
        get() = goals.count { it.isTargetReached }
}

/**
 * Estado de validación y datos para el formulario de creación/edición de metas.
 * Incluye soporte para selección opcional de Cuenta y Categoría.
 */
data class GoalFormState(
    val title: String = "",
    val targetAmount: String = "",
    val initialAmount: String = "",
    val deadline: String = "",
    val currency: String = "PEN",
    val color: String = "#34D399",
    val icon: String = "savings",

    // Cuenta y Categoría opcionales (NULL si no se seleccionan)
    val selectedAccountId: String? = null,
    val selectedAccountName: String? = null,
    val selectedCategoryId: String? = null,
    val selectedCategoryName: String? = null,

    // Catálogos disponibles para los selectores
    val availableAccounts: List<Account> = emptyList(),
    val availableCategories: List<Category> = emptyList(),

    // Errores de validación en tiempo real
    val titleError: String? = null,
    val targetAmountError: String? = null,
    val deadlineError: String? = null,
    val isSubmitting: Boolean = false
) {
    val isValid: Boolean
        get() = titleError == null &&
                targetAmountError == null &&
                deadlineError == null &&
                title.isNotBlank() &&
                targetAmount.isNotBlank() &&
                deadline.isNotBlank()
}
