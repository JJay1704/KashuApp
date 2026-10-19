package com.kashuapp.ui.goals.form

import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category

data class GoalFormState(
    val title: String = "",
    val isTitleError: String? = null,
    val targetAmount: String = "",
    val isTargetAmountError: String? = null,
    val initialAmount: String = "",
    val deadline: String = "",
    val isDeadlineError: String? = null,
    val currency: String = "PEN",
    val accountId: String? = null,
    val accountName: String = "",
    val categoryId: String? = null,
    val categoryName: String = "",
    val accounts: List<Account> = emptyList(),
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isEditing: Boolean = false
)