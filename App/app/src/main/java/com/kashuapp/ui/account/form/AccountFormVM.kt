package com.kashuapp.ui.account.form

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.accounType.AccountType
import com.kashuapp.data.accounType.AccountTypeRepository
import com.kashuapp.data.accounType.IAccountTypeRepository
import com.kashuapp.data.account.Account
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.currency.Currency
import com.kashuapp.data.currency.CurrencyRepository
import com.kashuapp.data.currency.ICurrencyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountFormVM(
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val accountTypeRepo: IAccountTypeRepository = AccountTypeRepository(),
    private val currencyRepo: ICurrencyRepository = CurrencyRepository()
) : ViewModel() {


    private val _uiState = MutableStateFlow(AccountFormState())
    val uiState = _uiState.asStateFlow()
    private var editingAccount: Account? = null


    init {

        loadAccountData()

    }

    fun loadAccountData() {
        viewModelScope.launch {


            _uiState.update { it.copy(isLoading = true, errorMessage = null) }


            val resultCurrency = currencyRepo.getAllCurrency()
            resultCurrency.onSuccess { list ->
                _uiState.update { it.copy(currency = list) }
            }
            resultCurrency.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }


            val result = accountTypeRepo.getAllAccountTypes()
            result.onSuccess { list ->
                _uiState.update { it.copy(accountTypes = list) }
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }

            _uiState.update { it.copy(isLoading = false) }
            editingAccount?.let { initForm(it) }

        }
    }

    fun onAccountType(selected: AccountType) {
        _uiState.update {
            it.copy(
                selectedAccountType = selected, isAccountTypeError = null
            )
        }
    }
    fun onCurrency(selected: Currency) {
        _uiState.update {
            it.copy(
                selectedCurrency = selected, isCurrencyError = null
            )
        }
    }

    fun onName(onName: String) {

        _uiState.update { it.copy(name = onName) }


    }

    fun initForm(accountToEdit: Account?) {
        editingAccount = accountToEdit
        if (accountToEdit != null) {
            val matchingType = _uiState.value.accountTypes.find {
                it.id.equals(accountToEdit.type, ignoreCase = true) ||
                        it.name.equals(accountToEdit.type, ignoreCase = true)
            }
            val matchingCurrency = _uiState.value.currency.find {
                it.code.equals(accountToEdit.currency, ignoreCase = true)
            }
            _uiState.update {
                it.copy(
                    name = accountToEdit.name,
                    isActive = accountToEdit.isActive,
                    initialBalance = accountToEdit.initialBalance.toString(),
                    selectedAccountType = matchingType,
                    isNameError = null,
                    isAccountTypeError = null,
                    isInitialBalanceError = null,
                    errorMessage = null,
                    selectedCurrency = matchingCurrency
                )
            }
        } else {
            resetForm()
        }

    }


    fun resetForm() {
        _uiState.update {
            it.copy(
                name = "",
                isActive = true,
                initialBalance = "0.00",
                selectedAccountType = null,
                selectedCurrency = null,
                isNameError = null,
                isAccountTypeError = null,
                isInitialBalanceError = null,
                isCurrencyError = null,
                errorMessage = null
            )
        }
    }

    fun onBalance(newBalance: String) {
        _uiState.update { it.copy(initialBalance = newBalance, isInitialBalanceError = null) }
    }


    fun saveAccount(accountToEdit: Account?, onSuccess: () -> Unit) {
        val state = _uiState.value
        val userId = authRepo.getCurrentUserId().id

        val nameError = if (state.name.trim().isBlank()) "Name is obligatory" else null
        val typeError = if (state.selectedAccountType == null) "Select a account type" else null
        val currencyError = if (state.selectedCurrency == null) "Select a currency" else null

        val balanceParsed = if (state.initialBalance.trim().isBlank()) {
            0.0
        } else {
            state.initialBalance.trim().replace(',', '.').toDoubleOrNull()
        }
        val balanceError = if (balanceParsed == null || !balanceParsed.isFinite() || balanceParsed < 0.0) {
            "Set a valid value"
        } else {
            null
        }


        if (nameError != null || typeError != null || currencyError != null || balanceError != null) {
            _uiState.update {
                it.copy(
                    isNameError = nameError,
                    isAccountTypeError = typeError,
                    isInitialBalanceError = balanceError,
                    isCurrencyError = currencyError
                )
            }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }



            val currentAccounts = accountRepo.getAllAccount(userId).getOrDefault(emptyList())
            val isStrictDuplicate = currentAccounts.any { existing ->
                val sameName = existing.name.trim().equals(state.name.trim(), ignoreCase = true)
                val sameType = existing.type.equals(state.selectedAccountType!!.id, ignoreCase = true)
                val sameCurrency = existing.currency.equals(state.selectedCurrency!!.code, ignoreCase = true)
                val isDifferentAccount = existing.id != accountToEdit?.id
                sameName && sameType && sameCurrency && isDifferentAccount
            }
            if (isStrictDuplicate) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Account is already existed"
                    )
                }
                return@launch
            }
            val validBalance = balanceParsed ?: 0.0

            val accountData = Account(
                id = accountToEdit?.id,
                userId = userId,
                name = state.name.trim(),
                type = state.selectedAccountType!!.id,
                currency = state.selectedCurrency!!.code,
                currentBalance = accountToEdit?.currentBalance ?: validBalance,
                initialBalance = validBalance,
                isActive = state.isActive,
            )

            val result = if (accountToEdit == null) {
                accountRepo.createAccount(accountData)
            } else {
                accountRepo.updateAccount(accountData)
            }

            _uiState.update { it.copy(isLoading = false) }

            result.onSuccess {
                resetForm()
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
        }
    }
}





