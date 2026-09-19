package com.kashuapp.ui.register.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.IAuthRepository
import com.kashuapp.data.SupabaseAuthRepository
import com.kashuapp.ui.register.model.RegisterScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(    private val authRepository: IAuthRepository = SupabaseAuthRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterScreenState())
    val uiState  = _uiState.asStateFlow()


    fun onLastName(newLastName:String){


        _uiState.update { it.copy(lastName = newLastName, errorMessage = "") }


    }

    fun onName(newLastName:String){


        _uiState.update { it.copy(name = newLastName, errorMessage = "") }


    }



    fun onEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, errorMessage = "") }
    }

    fun onPassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, errorMessage = "") }
    }

    fun onConfirmPassword(newConfirm: String) {
        _uiState.update { it.copy(confirmPassword = newConfirm, errorMessage = "") }
    }

    fun onTogglePassword() {
        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible) }
    }

    fun onToggleConfirmPassword() {
        _uiState.update { it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible) }
    }


    fun register(onSuccess: () -> Unit) {
        val email = uiState.value.email.trim()
        val pass = uiState.value.password
        val confirm = uiState.value.confirmPassword
        // 1. Validaciones locales
        if (email.isBlank() || !email.contains("@")) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres") }
            return
        }
        if (pass != confirm) {
            _uiState.update { it.copy(errorMessage = "Las contraseñas no coinciden") }
            return
        }
        // 2. Si todo está bien, llamamos a Supabase
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.signUp(email, pass)
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                onSuccess() // Navega directo al Home
            }
            result.onFailure {
                _uiState.update { it.copy(errorMessage = "Error al crear la cuenta o correo ya registrado") }
            }
        }
    }
}