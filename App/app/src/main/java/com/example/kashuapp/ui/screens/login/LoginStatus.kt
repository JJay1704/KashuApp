package com.example.kashuapp.ui.screens.login

data class LoginStatus(
    val usernameText: String = "",
    val passwordText: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isLoginSuccess: Boolean = false
)
