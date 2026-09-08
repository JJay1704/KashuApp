package com.example.kashuapp.ui.screens.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kashuapp.domain.usecase.UserLogic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val userLogic: UserLogic = UserLogic()
) : ViewModel() {

    private val _status = MutableStateFlow(LoginStatus())
    val status: StateFlow<LoginStatus> = _status.asStateFlow()

    fun onUsernameChange(newText: String) {
        _status.update { it.copy(usernameText = newText, errorMessage = null) }
    }

    fun onPasswordChange(newText: String) {
        _status.update { it.copy(passwordText = newText, errorMessage = null) }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            _status.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userLogic.executeLogin(_status.value.usernameText, _status.value.passwordText)
            result.onSuccess {
                _status.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }.onFailure { error ->
                _status.update { it.copy(isLoading = false, errorMessage = error.message ?: "Error desconocido") }
            }
        }
    }
}
