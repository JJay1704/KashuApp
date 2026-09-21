package com.kashuapp.ui.signUp

data class RegisterState(

    val fullName : String = "",
    val lastName : String = "",
    val fatherName : String = "",
    val motherName : String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = ""

)