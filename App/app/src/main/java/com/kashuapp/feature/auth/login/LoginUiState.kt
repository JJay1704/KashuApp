package com.kashuapp.feature.auth.login





data class LoginUiState(


    val email: String = "",
    val password: String = "",
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,      // ¿Está cargando la petición a Firebase?
    val errorMessage: String? = null,    // Si las credenciales son incorrectas (RF-01.4)
    val isSuccess: Boolean = false       // Si ya inició sesión para pasar al Dashboard



)
