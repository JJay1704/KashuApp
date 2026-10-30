package com.kashuapp.ui.goals.list

import com.kashuapp.data.goal.Goal
import com.kashuapp.data.account.Account

data class GoalsState(
    val goals: List<Goal> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val depositAmount: String = "",
    val depositAccountId: String? = null,
    val currencies: List<String> = listOf("PEN"),
    val selectedCurrency: String = "PEN",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isFormOpen: Boolean = false,
    val goalToEdit: Goal? = null,
    val isAddSavingsOpen: Boolean = false,
    val selectedGoalForSavings: Goal? = null,

    val totalTargetAmount: Double = 0.0,
    val totalSavedAmount: Double = 0.0,
    val overallProgressFraction: Float = 0.0f,
    val overallProgressPercentage: Int = 0,
    val completedGoalsCount: Int = 0
)
