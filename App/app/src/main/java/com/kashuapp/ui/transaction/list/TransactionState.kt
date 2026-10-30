package com.kashuapp.ui.transaction.list

import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category
import com.kashuapp.data.transaction.Transaction

data class TransactionState(
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val accounts: List<Account> = emptyList(),
    val selectedAccount: Account? = null,
    val selectedCategory: Category? = null,
    val selectedPeriod: String? = null,
    val selectedType: String? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
