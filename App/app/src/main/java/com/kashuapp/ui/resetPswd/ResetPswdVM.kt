package com.kashuapp.ui.resetPswd

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.onFailure
import kotlin.onSuccess

class ResetPswdVM(

    private val authRepo: IAuthRepository = AuthRepository()
) : ViewModel() {


    private val _uiState = MutableStateFlow(ResetPswdState())


    val uiState = _uiState.asStateFlow()
    fun onEmail(email: String) {
        if (_uiState.value.isLoading) return

        _uiState.update {
            it.copy(
                email = email,
                code = "",
                isError = null,
                successMessage = ""
            )
        }

        _uiState.update { it.copy(email = email) }

        if (email.isNullOrBlank()) {

            _uiState.update { it.copy(isError = null) }
        }
    }


    fun reset(onSuccess: () -> Unit) {

        val email = uiState.value.email.trim()

        val emailErr = when {
            email.isBlank() -> "Ingresa tu correo"
            !email.contains("@") -> "Correo electrónico inválido"
            else -> null
        }

        _uiState.update { it.copy(isError = emailErr) }


        if (emailErr == null) {

            _uiState.update {
                it.copy(
                    email = email,
                    isLoading = true,
                    isError = null,
                    successMessage = "",
                    code = ""
                )
            }

            viewModelScope.launch {

                _uiState.update { it.copy(isLoading = true, isError = null) }

                val result = authRepo.sendCode(email)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            successMessage = "Solicitud Enviada",
                            step = ResetSteps.CODE,
                            isError = null
                        )
                    }
                    _uiState.update { it.copy(isLoading = false) }

                }
                result.onFailure {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            successMessage = "",
                            isError = "No se pudo enviar el correo. Intenta más tarde."
                        )
                    }
                }

            }
        }


    }


    fun onCode(newCode: String) {
        _uiState.update { it.copy(code = newCode) }
    }

    fun confirmToken() {
        if (_uiState.value.isLoading) return
        val code = _uiState.value.code.trim()
        if (code.isBlank()) {
            _uiState.update { it.copy(isError = "Ingresa el código") }
            return
        }
        val email = _uiState.value.email

        _uiState.update {
            it.copy(isLoading = true, isError = null)
        }
        viewModelScope.launch {

            val result = authRepo.confirmToken(email, code)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        step = ResetSteps.NEW_PASSWORD,
                        isLoading = false,
                        isError = null
                    )
                }
            }
            result.onFailure {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = "El código no es correcto"
                    )
                }
            }

        }


    }

    fun togglePasswordVisibility() {
        _uiState.update {
            it.copy(isPasswordVisible = !it.isPasswordVisible)
        }
    }

    fun toggleConfirmPasswordVisibility() {
        _uiState.update {
            it.copy(isConfirmPasswordVisible = !it.isConfirmPasswordVisible)
        }
    }
    fun goBack() {
        _uiState.update {
            it.copy(
                step = when (it.step) {
                    ResetSteps.CODE -> ResetSteps.EMAIL
                    ResetSteps.NEW_PASSWORD -> ResetSteps.CODE
                    else -> it.step
                },
                isError = null
            )
        }
    }

    fun onNewPswd(newPswd: String) {
        _uiState.update { it.copy(newPswd = newPswd, isError = null) }
    }


    fun onConfirmPswd(confirmPswd: String) {
        _uiState.update { it.copy(newPswdConfirm = confirmPswd, isError = null) }
    }


    fun updatePswd() {
        if (_uiState.value.isLoading) return

        val password = _uiState.value.newPswd.orEmpty()

        val confirmPswd = _uiState.value.newPswdConfirm.orEmpty()
        val error = when {
            password.length < 8 -> "Debe tener al menos 8 caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            password != confirmPswd -> "Las contraseñas no coinciden"
            else -> null
        }

        if (error != null) {
            _uiState.update { it.copy(isError = error) }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, isError = null)
        }

        viewModelScope.launch {

            val result = authRepo.updatePassword(password)

            result.onSuccess {
                _uiState.update {
                    it.copy(
                        step = ResetSteps.COMPLETE,
                        isLoading = false,
                        newPswd = "",
                        newPswdConfirm = "",
                        code = "",
                        isError = null
                    )
                }
            }
            result.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isError = error.message
                            ?: "No se pudo actualizar la contraseña. Intenta nuevamente."
                    )
                }
            }
        }
    }

    fun resetFlow() {
        _uiState.value = ResetPswdState()
    }
}
