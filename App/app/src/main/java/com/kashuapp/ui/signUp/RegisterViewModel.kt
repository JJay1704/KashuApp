package com.kashuapp.ui.signUp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(    private val authRepository: IAuthRepository = AuthRepository()
): ViewModel() {
    private val _uiState = MutableStateFlow(RegisterState())
    val uiState  = _uiState.asStateFlow()




    fun onName(newLastName:String){
        _uiState.update { it.copy(fullName = newLastName, errorMessage = "") }
    }

    fun onFatherName(fatherName: String){

        _uiState.update { it.copy(fatherName = fatherName , errorMessage = "") }


    }
    fun onMotherName(motherName: String){

        _uiState.update { it.copy(motherName = motherName , errorMessage = "") }


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
        val givenName = uiState.value.fullName
        val fatherName = uiState.value.fatherName
        val motherName = uiState.value.motherName



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

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.signUp(email, pass, givenName,fatherName,motherName, fatherName + motherName  )
            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                onSuccess()
            }
            result.onFailure {error ->
                _uiState.update { it.copy(errorMessage = error.message ?: "Error desconocido") }
            }
        }
    }
}