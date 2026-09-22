package com.kashuapp.ui.transaction

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val authRepo : IAuthRepository = AuthRepository()
    ): ViewModel() {

    var transactions by mutableStateOf<List<Transaction>>(emptyList())
    var categories by mutableStateOf<List<Category>>(emptyList())





    fun loadCategories() {
        viewModelScope.launch {
            catRepo.getAllCateg(authRepo.getCurrentUserId().id).onSuccess { categories = it }
        }
    }
    fun getCategoryName(categoryId: String): String {
        return categories.find { it.id == categoryId }?.name ?: "Sin categoría"
    }
fun getTrans() {

    viewModelScope.launch {

        val result = transRepo.getAll()
        result.onSuccess { list ->
            transactions = list


    }
        result.onFailure { error ->
            println("Error al obtener transacciones: ${error.message}")
            }

}

}

}