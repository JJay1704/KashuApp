package com.kashuapp.ui.register.model

data class RegisterScreenState(

    val name : String = "",
    val lastName : String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""

)