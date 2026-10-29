package com.kashuapp.ui.login

import android.text.BoringLayout

data class LoginState(


    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSuccess: Boolean = false,
    val isOpenResetPswd : Boolean = false


)