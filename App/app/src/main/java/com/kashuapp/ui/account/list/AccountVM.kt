package com.kashuapp.ui.account.list

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.account.Account
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.ui.account.form.AccountFormState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AccountVM(
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
) : ViewModel() {


    var accounts by mutableStateOf<List<Account>>(emptyList())


    private val _uiState = MutableStateFlow(AccountFormState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun loadData() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val resultAccount = accountRepo.getAllAccount(authRepo.getCurrentUserId().id)
            resultAccount.onSuccess { list ->
                accounts = list
                _uiState.update { it.copy(accounts = list, isLoading = false) }
            }
            resultAccount.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun openEdit(account: Account) {
        _uiState.update { it.copy(accountToEdit = account, isFormOpen = true) }
    }

    fun deleteAccount(accountId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            accountRepo.deleteAccount(accountId).onSuccess {
                loadData()
            }.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun openForm() {
        _uiState.update { it.copy(accountToEdit = null, isFormOpen = true) }
        loadData()
    }


    fun closeForm() {

        _uiState.update { it.copy(accountToEdit = null, isFormOpen = false) }
        loadData()
    }
}

