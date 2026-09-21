package com.kashuapp.ui.addTrans
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.KashuSupaBase
import com.kashuapp.data.transaction.ITransactionRepository
import com.kashuapp.data.transaction.TransactionRepository
import com.kashuapp.data.accounts.Account
import com.kashuapp.data.accounts.AccountRepository
import com.kashuapp.data.accounts.IAccountRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
class AddTransVM(
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),
) : ViewModel(){
    var categories by mutableStateOf<List<Category>>(emptyList())
    var accounts by mutableStateOf<List<Account>>(emptyList())
    private val _uiStateTran = MutableStateFlow(AddTransState())
    val uiStateTran = _uiStateTran.asStateFlow()
    fun loadCategories() {
        viewModelScope.launch {
            val result = catRepo.getAllCateg()
            result.onSuccess { listaDesdeBaseDeDatos ->
                categories = listaDesdeBaseDeDatos // 👈 Se llena con las de Supabase
            }
            result.onFailure { error ->
                println("Error al cargar categorías: ${error.message}")
            }
        }
    }
    fun loadAccounts(){
        viewModelScope.launch {
            val resultAcc = accountRepo.getAllAccount()
            resultAcc.onSuccess { listdatabase -> accounts = listdatabase }
            resultAcc.onFailure { error -> println(error.message) }
        }
    }


    fun onType(newType : String){

        _uiStateTran.update { it.copy(type = newType) }


    }
    fun onAmount (newAmount : String){
         _uiStateTran.update { it.copy(amount = newAmount) }
     }
    fun onCategory(newCategory: Category){
         _uiStateTran.update { it.copy(categoryName = newCategory.name, categoryId = newCategory.id) }
     }
    fun onAccount(newAccount: Account){
        _uiStateTran.update { it.copy(accountName = newAccount.name, accountId = newAccount.id) }
    }
    fun onDate(newDate: String) {
        _uiStateTran.update { it.copy(date = newDate) }
    }
    fun onTime(newTime: String) {
        _uiStateTran.update { it.copy(time = newTime) }
    }
    fun onDescription (newDescription : String){
        _uiStateTran.update { it.copy(description = newDescription) }
    }
    fun postTrans (date: String, time: String, onSuccess: () -> Unit){

        val currentUser = KashuSupaBase.auth.currentUserOrNull()
        if (currentUser == null) {
            println("Error: No hay sesión activa de usuario")
            return
        }


        val stateValues = _uiStateTran.value
        val transPost = Transaction(

            userId = currentUser.id,
        amount = stateValues.amount.toDouble(),
        accountId = stateValues.accountId,
        categoryId = stateValues.categoryId,
        type =if (stateValues.type.isNotEmpty()) stateValues.type else "EXPENSE",
        description = stateValues.description,
        transactionDate = "$date $time"
        )
        viewModelScope.launch {
            val resultPost =transRepo.postTrans(transPost)
            resultPost.onSuccess {
                onSuccess() // 👈
            }
            resultPost.onFailure {error ->  println(error)}
        }
    }





}




