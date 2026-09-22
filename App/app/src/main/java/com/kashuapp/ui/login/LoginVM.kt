package com.kashuapp.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginVM(
    private val authRepo : IAuthRepository = AuthRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow(LoginState())
    val uiState  = _uiState.asStateFlow()




    fun onEmail (newEmail: String){
        _uiState.update { it.copy(email = newEmail, errorMessage = "" )}
    }
    fun onPassword (newPassword: String){

        _uiState.update { it.copy(password= newPassword, errorMessage = "" )}

    }

    fun onTogglePassword() {

        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible, errorMessage = "") }


    }

    fun loginSuccess (email : String = uiState.value.email, password : String =uiState.value.password ,  onSucces : ()->Unit) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Por favor, ingresa tu correo y contraseña") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = "") }



        val result = authRepo.login(email,password)

        _uiState.update { it.copy(isLoading = false) }


        result.onSuccess {
            onSucces()
        }
        result.onFailure {

            _uiState.update { it.copy(errorMessage = "Correo o contraseña incorrectos") }
        }

    }
    }






}