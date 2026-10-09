package com.kashuapp.ui.transaction.list

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.kashuapp.data.account.Account
import com.kashuapp.data.account.AccountRepository
import com.kashuapp.data.account.IAccountRepository
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository

import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.launch

class TransactionVM(

    private val transRepo: ITransactionRepository = TransactionRepository(),

    private val catRepo: ICategoryRepository = CategoryRepository(),
    private var authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository()
) : ViewModel() {

    init {
        loadData()
    }


    var transactions by mutableStateOf<List<Transaction>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var accounts by mutableStateOf<List<Account>>(emptyList())
    fun loadData() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val resultTrans = transRepo.getAll(userId, null)
            val resultCat = catRepo.getAllCateg(userId)
            val resultAccount = accountRepo.getAllAccount(userId)
            resultTrans.onSuccess { list -> transactions = list }
            resultTrans.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultCat.onSuccess { list -> categories = list }
            resultCat.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultAccount.onSuccess { list -> accounts = list }
            resultTrans.onFailure { error -> println("Error al obtener cuentas: ${error.message}") }
        }
    }


    fun getCategoryName(categoryId: String): String {
        return categories.find { it.id == categoryId }?.name ?: "Sin categoría"

    }

    fun getAccountName(accountId: String?): String {
        return accounts.find { it.id == accountId }?.name ?: "Sin Cuenta"

    }

    var selectedAccountId by mutableStateOf<String?>(null)

    fun selectAccount(accountId: String?) {
        selectedAccountId = accountId
        getTrans(
            selectedAccountId
        )
    }

    fun getTrans(filter: String? = selectedAccountId) {


        if (filter != null) {
            viewModelScope.launch {
                val userId = authRepo.getCurrentUserId().id

                val result = transRepo.getAll(userId, filter)
                result.onSuccess { list -> transactions = list }
                result.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
                val resultAccount = accountRepo.getAllAccount(userId)
                resultAccount.onSuccess { list -> accounts = list }
            }
        } else {
            viewModelScope.launch {
                val userId = authRepo.getCurrentUserId().id

                val result = transRepo.getAll(userId, null)
                result.onSuccess { list -> transactions = list }
                result.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }


                val resultAccount = accountRepo.getAllAccount(userId)
                resultAccount.onSuccess { list -> accounts = list }
                resultAccount.onFailure { error -> println("Error al actualizar cuentas: ${error.message}") }

            }

        }

    }


    fun getBalance(accountId: String?): String {


        val accountById: Account? = accounts.find { it.id == accountId }


        return if (accountById != null) {
            " ${accountById.currentBalance} ${accountById.currency}"

        } else {
            " 0.00"
        }


    }


}