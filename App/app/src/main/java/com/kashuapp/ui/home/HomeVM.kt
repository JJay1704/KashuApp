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
import com.kashuapp.core.finance.FinanceRules
import com.kashuapp.core.finance.CurrencyBalance
import com.kashuapp.data.accounType.AccountTypeRepository
import com.kashuapp.data.accounType.IAccountTypeRepository
import com.kashuapp.data.goal.GoalRepository
import com.kashuapp.data.goal.IGoalRepository

class HomeVM(
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val typeRepo: IAccountTypeRepository = AccountTypeRepository(),
    private val goalRepo: IGoalRepository = GoalRepository()
) : ViewModel() {

    var accounts by mutableStateOf<List<Account>>(emptyList())
    var accountsByCurrency by mutableStateOf<List<AccountByCurrency>>(emptyList())
    var balances by mutableStateOf<List<CurrencyBalance>>(emptyList())
    var errorMessage by mutableStateOf<String?>(null)

    fun loadData() {


        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val resultTrans = transRepo.getAll(userId, null, null, null)
            val resultCat = catRepo.getAllCateg(userId)
            val resultAccount = accountRepo.getAllAccount(userId)
            val resultTypes = typeRepo.getAllAccountTypes()
            val resultGoals = goalRepo.getAllGoals(userId)
            resultTypes.onFailure { errorMessage = "No se pudo calcular el patrimonio: ${it.message}" }
            if (resultAccount.isSuccess && resultTypes.isSuccess && resultGoals.isSuccess) {
                balances = FinanceRules.balances(resultAccount.getOrThrow(), resultTypes.getOrThrow(), resultGoals.getOrThrow())
                errorMessage = null
            } else errorMessage = "No se pudo actualizar el patrimonio. Intenta nuevamente."
            resultTrans.onSuccess { list -> transactions = list.sortedByDescending { FinanceRules.timestampMillis(it.transactionDate) }.take(5) }
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

