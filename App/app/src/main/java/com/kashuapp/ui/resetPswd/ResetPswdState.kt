package com.kashuapp.ui.resetPswd

data class ResetPswdState(
    val email: String = "",
    val isError: String? = null,
    val successMessage: String = "",
    val step: ResetSteps = ResetSteps.EMAIL,
    val code: String = "",
    val isLoading: Boolean = false,
    val newPswd: String = "",
    val newPswdConfirm : String = "",
    val isErrorPswd : String= "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
)
