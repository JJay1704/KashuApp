package com.kashuapp.ui.category

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import com.kashuapp.data.category.Category
import com.kashuapp.data.category.CategoryRepository
import com.kashuapp.data.category.ICategoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CategoryVM(
    private val catRepo: ICategoryRepository = CategoryRepository(),
    private val authRepo: IAuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(CategoryState())
    val uiState = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = authRepo.getCurrentUserId().id
            val result = catRepo.getAllCateg(userId)
            result.onSuccess { list ->
                _uiState.update { it.copy(categories = list, isLoading = false) }
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun openCreate() {
        _uiState.update { it.copy(categoryToEdit = null, isFormOpen = true, errorMessage = null) }
    }

    fun openEdit(category: Category) {
        _uiState.update { it.copy(categoryToEdit = category, isFormOpen = true, errorMessage = null) }
    }

    fun closeForm() {
        _uiState.update { it.copy(categoryToEdit = null, isFormOpen = false, errorMessage = null) }
    }

    fun saveCategory(
        name: String,
        type: String,
        onSuccess: () -> Unit
    ) {
        val userId = authRepo.getCurrentUserId().id

        if (name.trim().isBlank()) {
            _uiState.update { it.copy(errorMessage = "El nombre no puede estar vacío") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val currentCategory = _uiState.value.categoryToEdit

            val result: Result<Unit> = if (currentCategory != null) {
                val updatedCat = currentCategory.copy(
                    name = name.trim(),
                    type = type.uppercase()
                )
                catRepo.updateCategory(updatedCat)
            } else {
                val newCat = Category(
                    userId = userId,
                    name = name.trim(),
                    type = type.uppercase(),
                    icon = "category",
                    color = "#34D399"
                )
                catRepo.createCategory(newCat).map { Unit }
            }

            result.onSuccess {
                closeForm()
                loadCategories()
                onSuccess()
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }

    fun deleteCategory(categoryId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val result = catRepo.deleteCategory(categoryId)
            result.onSuccess {
                loadCategories()
            }
            result.onFailure { error ->
                _uiState.update { it.copy(errorMessage = error.message, isLoading = false) }
            }
        }
    }
}
