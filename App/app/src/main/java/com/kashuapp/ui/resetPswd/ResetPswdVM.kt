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


        _uiState.update { it.copy(email = email) }

        if (email.isNullOrBlank()) {

            _uiState.update { it.copy(isError = null) }
        }
    }


    fun reset(onSuccess: () -> Unit) {

        val email = uiState.value.email

        val emailErr = when {
            email.isBlank() -> "Ingresa tu correo"
            !email.contains("@") -> "Correo electrónico inválido"
            else -> null
        }


        _uiState.update { it.copy(isError = emailErr) }


        if (emailErr == null) {

            viewModelScope.launch {


                val result = authRepo.sendCode(email)
                result.onSuccess {
                    _uiState.update {
                        it.copy(
                            successMessage = "Solicitud Enviada",
                            step = ResetSteps.CODE,
                            isError = null
                        )
                    }

                }
                result.onFailure { _uiState.update { it.copy(successMessage = "No se pudo procesar la solicitud") } }

            }
        }


    }


    fun onCode(newCode: String) {
        _uiState.update { it.copy(code = newCode) }
    }

    fun confirmToken() {

        viewModelScope.launch {

            val result = authRepo.confirmToken(_uiState.value.email, _uiState.value.code)
            result.onSuccess {
                _uiState.update {
                    it.copy(
                        step = ResetSteps.NEW_PASSWORD,
                        isError = null
                    )
                }
            }
            result.onFailure { _uiState.update { it.copy(isError = " El codigo no es correcto") } }

        }


    }

    fun onNewPswd(newPswd: String) {
        _uiState.update { it.copy(newPswd = newPswd, isError = null) }
    }


    fun onConfirmPswd(confirmPswd: String) {
        _uiState.update { it.copy(newPswdConfirm = confirmPswd, isError = null) }
    }


    fun updatePswd() {


        val password = _uiState.value.newPswd.orEmpty()

        val confirmPswd = _uiState.value.newPswdConfirm.orEmpty()
        val error = when {
            password.length < 8 -> "Debe tener al menos 8 caracteres"
            !password.any { it.isUpperCase() } -> "Debe incluir una mayúscula"
            !password.any { it.isDigit() } -> "Debe incluir un número"
            password!=confirmPswd -> "Las contraseñas no coinciden"
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
                        newPswd = "",

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