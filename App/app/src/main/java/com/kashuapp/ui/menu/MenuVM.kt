package com.kashuapp.ui.menu

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository
import kotlinx.coroutines.launch

class MenuVM(
    private val authrepo: IAuthRepository = AuthRepository()

) : ViewModel() {


    fun logOut(onSuccess: () -> Unit) {

        viewModelScope.launch {
            val result = authrepo.logOut()
            result.onSuccess { onSuccess()}
        }

    }


    fun getUserTexts(): List<String> {
        val getUser = authrepo.getCurrentUserId()
        val firstInitial = getUser.fullName.first().uppercase()
        val secondInitial = getUser.fatherName.first().uppercase()
        val initials = firstInitial + secondInitial
        val listText = mutableListOf(getUser.fullName, initials, getUser.email)
        return listText
    }
}





