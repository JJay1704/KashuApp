package com.kashuapp.ui.addCategory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import com.kashuapp.data.transaction.Transaction
import com.kashuapp.ui.login.LoginState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddCategoryVM(

    private val categoryRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository(),


    ) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCategoryState())
    val uiState = _uiState.asStateFlow()


    fun onName(newName: String) {


        _uiState.update { it.copy(name = newName) }

    }

    fun onType(newType: String) {


        _uiState.update { it.copy(type = newType) }
    }


    fun postCategory() {

        val currentUserId = authRepo.getCurrentUserId()
        val stateValues = _uiState.value



        if (stateValues.name.trim().isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }


        if (stateValues.name.length < 3) {
            _uiState.update { it.copy(errorMessage = "El nombre debe tener al menos 3 caracteres") }
            return
        }
        val categoryPost = Category(

            userId = currentUserId.id,
            name = stateValues.name,
            type = stateValues.type.uppercase().ifEmpty { "EXPENSE" },

            icon = stateValues.icon,
            color = stateValues.color,
        )



        viewModelScope.launch {

            val result = categoryRepo.createCategory(categoryPost)
            result.onSuccess {
                println("✅ Categoría guardada en Supabase: $it")
            }
            result.onFailure { error ->
                println("❌ Error de Supabase: ${error.message}")
            }
        }
    }


}