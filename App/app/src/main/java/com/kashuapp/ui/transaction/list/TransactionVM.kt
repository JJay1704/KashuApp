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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TransactionVM(
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository()

) : ViewModel() {




    init {
        loadData()
    }

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())
    var accounts by mutableStateOf<List<Account>>(emptyList())


    val periods = listOf("Today", "This week", "This Month", "Last Month")
    var selectedPeriod by mutableStateOf<String?>(null)




    var selectedAccount by mutableStateOf<Account?>(null)
    var selectedCategory by mutableStateOf<Category?>(null)
    val transactionTypes = listOf("EXPENSE", "INCOME")
    var selectedType by mutableStateOf<String?>(null)

    fun selectedType(Type: String?) {
        selectedType = Type
        getTrans()
    }
    fun getDateRange(option: String): Pair<String, String?> {

        val calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val todayStr = sdf.format(calendar.time)
        return when (option) {
            periods[0] -> {
                Pair("$todayStr 00:00", "$todayStr 23:59")
            }

            periods[1] -> {
                calendar.add(Calendar.DAY_OF_YEAR, -7)
                val weekStart = sdf.format(calendar.time)
                Pair("$weekStart 00:00", null)
            }

            periods[2] -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val monthStart = sdf.format(calendar.time)
                Pair("$monthStart 00:00", null)
            }

            periods[3] -> {
                calendar.add(Calendar.MONTH, -1)
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val lastMonthStart = sdf.format(calendar.time)
                val maxDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
                calendar.set(Calendar.DAY_OF_MONTH, maxDay)
                val lastMonthEnd = sdf.format(calendar.time)
                Pair("$lastMonthStart 00:00", "$lastMonthEnd 23:59")
            }

            else -> Pair("$todayStr 00:00", "$todayStr 23:59")
        }
    }


    fun loadData() {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val resultTrans = transRepo.getAll(userId, null, null, null)
            val resultCat = catRepo.getAllCateg(userId)
            val resultAccount = accountRepo.getAllAccount(userId)
            resultTrans.onSuccess { list -> transactions = list.sortedByDescending {it.transactionDate}  }
            resultTrans.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultCat.onSuccess { list -> categories = list }
            resultCat.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            resultAccount.onSuccess { list -> accounts = list }
            resultAccount.onFailure { error -> println("Error al obtener cuentas: ${error.message}") }
        }
    }


    fun getCategoryName(categoryId: String): String {
        return categories.find { it.id == categoryId }?.name ?: "Sin categoría"
    }





    fun getAccountName(accountId: String?): String {
        return accounts.find { it.id == accountId }?.name ?: "Sin Cuenta"
    }


    fun selectAccount(account: Account?) {
        selectedAccount = account
        getTrans()
    }

    fun selectCategory(category: Category?) {
        selectedCategory = category
        getTrans()
    }
    fun selectPeriod(period: String?) {
        selectedPeriod = period
        getTrans()
    }
    fun getTrans(
        filterAccount: String? = selectedAccount?.id,
        filterCategory: String? = selectedCategory?.id,
        period: String? = selectedPeriod,
        type: String ? = selectedType



    ) {
        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id

            val (startDate, endDate) = if (period != null) getDateRange(period) else Pair(null, null)

            val result = transRepo.getAll(userId, filterAccount, filterCategory, null, startDate, endDate, type)
            result.onSuccess { list -> transactions = list.sortedByDescending { it.transactionDate } }
            result.onFailure { error -> println("Error al obtener transacciones: ${error.message}") }
            val resultAccount = accountRepo.getAllAccount(userId)
            resultAccount.onSuccess { list -> accounts = list }
            resultAccount.onFailure { error -> println("Error al actualizar cuentas: ${error.message}") }
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