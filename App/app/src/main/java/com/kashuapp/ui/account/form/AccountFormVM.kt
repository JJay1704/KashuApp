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
        if (accountToEdit != null) {
            val matchingType = _uiState.value.accountTypes.find {
                it.name.equals(
                    accountToEdit.type, ignoreCase = true
                )
            }
            _uiState.update {
                it.copy(
                    name = accountToEdit.name,
                    initialBalance = accountToEdit.currentBalance.toString(),
                    selectedAccountType = matchingType,
                    isNameError = null,
                    isAccountTypeError = null,
                    isInitialBalanceError = null,
                    errorMessage = null
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


    fun loadAccounts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = authRepo.getCurrentUserId().id
            val result = accountRepo.getAllAccount(userId)
            result.onSuccess { list ->
                _uiState.update { it.copy(accounts = list, isLoading = false) }
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun openCreate() {
        _uiState.update { it.copy(accountToEdit = null, isFormOpen = true, errorMessage = null) }
    }


    fun openEdit(Account: Account) {


        _uiState.update { it.copy(accountToEdit = Account, isFormOpen = true, errorMessage = null) }


    }

    fun deleteAccount(accountId: String) {


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = accountRepo.deleteAccount(accountId)
            result.onSuccess {
                loadAccounts()
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun closeForm() {

        _uiState.update { it.copy(isFormOpen = false, errorMessage = null) }


    }

    fun saveAccount(accountToEdit: Account?, onSuccess: () -> Unit) {
        val state = _uiState.value
        val userId = authRepo.getCurrentUserId().id

        val nameError = if (state.name.trim().isBlank()) "El nombre es obligatorio" else null
        val typeError = if (state.selectedAccountType == null) "Selecciona un tipo de cuenta" else null
        val currencyError = if (state.selectedCurrency == null) "Selecciona una moneda" else null

        val balanceParsed = if (state.initialBalance.trim().isBlank()) {
            0.0
        } else {
            state.initialBalance.trim().replace(',', '.').toDoubleOrNull()
        }
        val balanceError = if (balanceParsed == null || balanceParsed < 0.0) {
            "Ingrese un saldo válido (>= 0)"
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
                        errorMessage = "Ya existe una cuenta con el mismo nombre, tipo y moneda"
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
                currentBalance = validBalance
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





