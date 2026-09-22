package com.kashuapp.ui.addCategory

data class AddCategoryState(

    val id: String? = null,
    val userId: String? = null,
    val name: String = "",
    val type: String = "",
    val icon: String = "",
    val color: String = "",
    val errorMessage: String = ""


)