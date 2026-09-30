package com.kashuapp.ui.signUp

data class SignUpState(

    val fullName : String = "",
    val isFullNameError: String? =null,
    val email: String = "",
    val isEmailError:String? =null,
    val lastName : String = "",
    val fatherName : String = "",
    val isErrorFatherName : String? =null ,

    val motherName : String = "",
    val isErrorMotherName : String? =null,

    val password: String = "",
    val isPasswordError:String? =null,

    val confirmPassword: String = "",
    val isPasswordVisible: Boolean = false,
    val isConfirmPasswordVisible: Boolean = false,
    val confirmPasswordError: String?= null,
    val isLoading: Boolean = false,
    val errorMessage: String = ""

)