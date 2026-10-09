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
            val resultAccount = accountRepo.getAllAccount(authRepo.getCurrentUserId().id)
            resultAccount.onSuccess { list ->
                accounts = list
            }
            resultAccount.onFailure { error ->
                println("Error al obtener cuentas: ${error.message}")
            }
        }
    }




    fun openForm() {

        _uiState.update { it.copy(isFormOpen = true) }
    }


    fun closeForm() {

        _uiState.update { it.copy(isFormOpen = false) }
    }
}

