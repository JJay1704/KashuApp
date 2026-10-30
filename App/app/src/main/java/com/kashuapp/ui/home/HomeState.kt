package com.kashuapp.ui.home

import com.kashuapp.data.account.Account
import com.kashuapp.data.category.Category
import com.kashuapp.data.transaction.Transaction

data class HomeState(
    val accounts: List<Account> = emptyList(),
    val accountsByCurrency: List<AccountByCurrency> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
