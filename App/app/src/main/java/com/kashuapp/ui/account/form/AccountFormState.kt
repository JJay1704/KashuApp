package com.kashuapp.ui.account.form

import com.kashuapp.data.accounType.AccountType
import com.kashuapp.data.account.Account
import com.kashuapp.data.currency.Currency

data class AccountFormState(
    val accounts: List<Account> = emptyList(),
    val currency: List<Currency> = emptyList(),
    val accountTypes: List<AccountType> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,

    val isFormOpen: Boolean = false,
    val accountToEdit: Account? = null,
    val name: String = "",
    val initialBalance: String = "0.00",
    val selectedAccountType: AccountType? = null,
    val selectedCurrency: Currency? = null,
    val isNameError: String? = null,
    val isAccountTypeError: String? = null,
    val isInitialBalanceError: String? = null,
    val isCurrencyError: String? = null
)