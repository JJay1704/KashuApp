package com.kashuapp.ui.login

data class LoginState(


    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false



)