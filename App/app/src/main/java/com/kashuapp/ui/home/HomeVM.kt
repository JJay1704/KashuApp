package com.kashuapp.ui.home

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
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.launch
import com.kashuapp.data.transaction.Transaction

class HomeVM(
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository()
) : ViewModel() {

    var accounts by mutableStateOf<List<Account>>(emptyList())
    var accountsByCurrency by mutableStateOf<List<AccountByCurrency>>(emptyList())

    fun loadData() {


        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val resultTrans = transRepo.getAll(userId, null, null, null)
            val resultCat = catRepo.getAllCateg(userId)
            val resultAccount = accountRepo.getAllAccount(userId)
            resultTrans.onSuccess { list -> transactions = list.sortedByDescending {it.transactionDate}.take(5) }
            resultTrans.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultCat.onSuccess { list -> categories = list }
            resultCat.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultAccount.onSuccess { list -> accounts = list }
            resultAccount.onFailure { error -> println("Error al obtener cuentas: ${error.message}") }


            resultAccount.onSuccess { list ->
                accounts = list
                accountsByCurrency = list.filter { it.isActive }.groupBy { it.currency }
                    .map { (currency, accountsInThisCurrency) ->
                        val total = accountsInThisCurrency.sumOf { it.currentBalance }
                        val currencyType = currency
                        AccountByCurrency(
                            currency = currency,
                            totalAmount = total,
                            formattedTotal = "$currencyType $total"
                        )
                    }
            }
            resultAccount.onFailure { error -> println("Error al obtener cuentas: ${error.message}") }
        }
    }
    init {
        loadData()
    }
    var transactions by mutableStateOf<List<Transaction>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    fun getCategoryName(categoryId: String): String {
        return categories.find { it.id == categoryId }?.name ?: "Sin categoría"
    }
    fun getAccountName(accountId: String?): String {
        return accounts.find { it.id == accountId }?.name ?: "Sin Cuenta"
    }
}

