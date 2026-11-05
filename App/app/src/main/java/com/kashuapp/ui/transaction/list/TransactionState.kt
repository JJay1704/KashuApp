package com.kashuapp.ui.transaction.list

import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category

data class TransactionState(
    val movements: List<HistoryMovement> = emptyList(),
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val selectedPeriod: String? = null,
    val selectedType: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
