package com.kashuapp.ui.transaction.form

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
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.data.transaction.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TransactionFormVM(
    private val transRepo: ITransactionRepository = TransactionRepository(),
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),
    private val accountRepo: IAccountRepository = AccountRepository(),

    ) : ViewModel() {

    private val _uiStateTran = MutableStateFlow(TransactionFormState())
    val uiStateTran = _uiStateTran.asStateFlow()
    var categories by mutableStateOf<List<Category>>(emptyList())


    fun initForm(
        trans: Transaction?,
        defaultDate: String,
        defaultTime: String,
        initialAccountName: String = "",

        initialCategoryName: String = ""
    ) {
        if (trans != null) {
            val rawDate = trans.transactionDate.replace("T", " ").trim()
            val dateParts = rawDate.split(" ")
            val date =
                if (dateParts.isNotEmpty() && dateParts[0].length >= 10) dateParts[0].substring(
                    0,
                    10
                ) else defaultDate
            val time = if (dateParts.size > 1 && dateParts[1].length >= 5) dateParts[1].substring(
                0,
                5
            ) else defaultTime

            _uiStateTran.value = TransactionFormState(
                id = trans.id,
                userId = trans.userId,
                selectedTypeTrans = trans.type,
                amount = trans.amount.toString(),
                accountId = trans.accountId,
                accountName = initialAccountName,
                categoryId = trans.categoryId,
                categoryName = initialCategoryName,
                date = date,
                time = time,
                description = trans.description ?: "",
                isEditing = true
            )
        } else {
            _uiStateTran.value = TransactionFormState(
                date = defaultDate,
                time = defaultTime,
                isEditing = false
            )
        }

        viewModelScope.launch {
            val userId = authRepo.getCurrentUserId().id
            val catList = catRepo.getAllCateg(userId).getOrDefault(emptyList())
            val accList = accountRepo.getAllAccount(userId).getOrDefault(emptyList())
            categories = catList

            val account = accList.find { it.id == _uiStateTran.value.accountId }
            val category = catList.find { it.id == _uiStateTran.value.categoryId }

            _uiStateTran.update { current ->
                current.copy(
                    categories = catList,
                    accounts = accList,
                    accountName = account?.name ?: current.accountName,
                    categoryName = category?.name ?: current.categoryName
                )
            }
        }
    }

    fun deleteTrans(transId: String?, onSuccess: () -> Unit) {
        if (transId == null) return

        viewModelScope.launch {

            val result = transRepo.deleteTrans(transId)
            result.onSuccess {
                onSuccess()
                resetForm()
            }
            result.onFailure { error ->
                println(error)

            }


        }
    }
//
//    fun loadCategories() {
//        viewModelScope.launch {
//            val userId = authRepo.getCurrentUserId().id
//            catRepo.getAllCateg(userId).onSuccess { list ->
//                categories = list
//
//                _uiStateTran.update { it.copy(categories = list) }
//            }
//        }
//    }
//
//    fun loadAccounts() {
//        viewModelScope.launch {
//            val currentUser = authRepo.getCurrentUserId()
//            val resultAcc = accountRepo.getAllAccount(currentUser.id)
//            resultAcc.onSuccess { listDB ->
//                _uiStateTran.update { it.copy(accounts = listDB) }
//
//
//            }
//            resultAcc.onFailure { error -> println(error.message) }
//        }
//    }

    fun onType(selectedType: String) {

        _uiStateTran.update { it.copy(selectedTypeTrans = selectedType) }


    }

    fun onAmount(newAmount: String) {
        _uiStateTran.update { it.copy(amount = newAmount, isAmountError = null) }
    }

    fun resetForm() {
        _uiStateTran.value = TransactionFormState(
            categories = categories, accounts = _uiStateTran.value.accounts
        )
    }

    fun onAccount(newAccount: Account) {
        _uiStateTran.update {
            it.copy(
                accountName = newAccount.name,

                accountId = newAccount.id ?: "", isAccountError = null

            )
        }
    }

    fun onCategory(newCategory: Category) {
        _uiStateTran.update {
            it.copy(

                categoryName = newCategory.name,
                categoryId = newCategory.id ?: "",
                isCategoryError = null
            )
        }
    }

    fun onDate(newDate: String) {
        _uiStateTran.update { it.copy(date = newDate) }
    }

    fun onTime(newTime: String) {
        _uiStateTran.update { it.copy(time = newTime) }
    }

    fun onDescription(newDescription: String) {
        _uiStateTran.update { it.copy(description = newDescription) }
    }


    fun postTrans(date: String, time: String, onSuccess: () -> Unit) {
        val currentUserId = authRepo.getCurrentUserId()
        val stateValues = _uiStateTran.value
        val amountDouble = stateValues.amount.toDoubleOrNull()
        val errorAmount =
            if (amountDouble == null || amountDouble <= 0.0) "Ingrese monto valido mayor a 0  " else null

        val errorAccount = if (stateValues.accountId.isBlank()) "Seleccione Cuenta valida" else null

        val errorCategory =
            if (stateValues.categoryId.isBlank()) "Seleccione Categoria valida" else null

        val hasAnyError = errorAmount != null || errorAccount != null || errorCategory != null
        if (hasAnyError) {
            _uiStateTran.update {
                it.copy(
                    isAmountError = errorAmount,
                    isAccountError = errorAccount,
                    isCategoryError = errorCategory,
                )
            }
            return
        }
        val transPost = Transaction(
            id = stateValues.id,
            userId = currentUserId.id,
            amount = amountDouble!!,
            accountId = stateValues.accountId,
            categoryId = stateValues.categoryId,
            type = stateValues.selectedTypeTrans.ifEmpty { "EXPENSE" },
            description = stateValues.description,
            transactionDate = "$date $time"
        )
        viewModelScope.launch {
            val resultPost = if (stateValues.id != null) {

                transRepo.updateTrans(transPost)
            } else {
                transRepo.postTrans(transPost)

            }
            resultPost.onSuccess {
                onSuccess()


                resetForm()
            }
            resultPost.onFailure { error -> println(error) }
        }
    }

}
