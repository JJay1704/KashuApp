package com.kashuapp.ui.signUp

import androidx.annotation.Nullable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SignUpVM(
    private val authRepository: IAuthRepository = AuthRepository()
) : ViewModel() {
    private val _uiState = MutableStateFlow(SignUpState())
    val uiState = _uiState.asStateFlow()
    fun onName(newLastName: String) {
        _uiState.update { it.copy(fullName = newLastName, isFullNameError = null) }
    }

    fun onFatherName(fatherName: String) {

        _uiState.update { it.copy(fatherName = fatherName, isErrorFatherName = null) }


    }

    fun onMotherName(motherName: String) {

        _uiState.update { it.copy(motherName = motherName, isErrorMotherName = null) }


    }

    fun onEmail(newEmail: String) {
        _uiState.update { it.copy(email = newEmail, isEmailError = null) }
    }

    fun onPassword(newPassword: String) {
        _uiState.update { it.copy(password = newPassword, isPasswordError = null) }
    }

    fun onConfirmPassword(newConfirm: String) {
        _uiState.update { it.copy(confirmPassword = newConfirm, isPasswordError = null) }
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


        val nameErr = if (givenName.isBlank()) "Ingresa tu nombre" else null
        val fatherErr = if (fatherName.isBlank()) "Ingresa tu apellido paterno" else null
        val emailErr = when {
            email.isBlank() -> "Ingresa tu correo"
            !email.contains("@") -> "Correo electrónico inválido"
            else -> null
        }
        val passwordErr = when {
            pass.length < 8 -> "Debe tener al menos 8 caracteres"
            !pass.any { it.isUpperCase() } -> "Debe incluir al menos una mayúscula"
            !pass.any { it.isDigit() } -> "Debe incluir al menos un número"
            else -> null
        }
        val confirmErr = if (pass != confirm) "Las contraseñas no coinciden" else null

        _uiState.update {
            it.copy(
                isFullNameError = nameErr,
                isErrorFatherName = fatherErr,
                isEmailError = emailErr,
                isPasswordError = passwordErr,
                confirmPasswordError = confirmErr
            )
        }

        val hasAnyError =
            listOf(nameErr, fatherErr, emailErr, passwordErr, confirmErr).any { it != null }
        if (hasAnyError) return


        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }
            val result = authRepository.signUp(
                email, pass, givenName, fatherName, motherName, "${fatherName} ${motherName} "
            )

            _uiState.update { it.copy(isLoading = false) }
            result.onSuccess {
                onSuccess()
            }
            result.onFailure { error ->
                val rawMessage = error.message ?: ""

                if (rawMessage.contains("User already registered", ignoreCase = true)) {
                    _uiState.update { it.copy(isEmailError = "Este correo ya está registrado") }
                } else {
                    _uiState.update { it.copy(errorMessage = "Ocurrió un error al registrar la cuenta") }
                }
            }
        }
    }
}