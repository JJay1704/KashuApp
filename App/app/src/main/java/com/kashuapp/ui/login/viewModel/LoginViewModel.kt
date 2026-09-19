package com.kashuapp.ui.login.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.SupabaseAuthRepository
import com.kashuapp.data.IAuthRepository
import com.kashuapp.ui.login.model.LoginScreenState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repositoryFireBase : IAuthRepository = SupabaseAuthRepository()
) : ViewModel(){

    private val _uiState = MutableStateFlow(LoginScreenState())
    val uiState  = _uiState.asStateFlow()




    fun onEmail (newEmail: String){
        _uiState.update { it.copy(email = newEmail, errorMessage = "" )}
    }
    fun onPassword (newPaswword: String){

        _uiState.update { it.copy(password= newPaswword, errorMessage = "" )}

    }

    fun onTogglePassword() {

        _uiState.update { it.copy(isPasswordVisible = !it.isPasswordVisible, errorMessage = "") }


    }

    fun loginSuccess (email : String = uiState.value.email, password : String =uiState.value.password ,  onSucces : ()->Unit) {

    viewModelScope.launch {

        _uiState.update { it.copy(isLoading = true, errorMessage = "") }



        val result = repositoryFireBase.login(email,password)

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