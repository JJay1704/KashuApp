package com.kashuapp.ui.category

import com.kashuapp.data.category.Category

data class CategoryState(
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val categoryToEdit: Category? = null,
    val isFormOpen: Boolean = false
)
