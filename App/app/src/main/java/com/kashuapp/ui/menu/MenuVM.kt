package com.kashuapp.ui.menu

import androidx.lifecycle.ViewModel
import com.kashuapp.data.auth.AuthRepository
import com.kashuapp.data.auth.IAuthRepository

class MenuVM(
    private val authrepo: IAuthRepository = AuthRepository()

) : ViewModel() {


    fun getUserTexts(): List<String> {


        val getUser = authrepo.getCurrentUserId()

        val firstInitial = getUser.fullName.first().uppercase()
        val secondInitial = getUser.fatherName.first().uppercase()
        val initials = firstInitial+secondInitial


        val listText = mutableListOf(getUser.fullName,initials,getUser.email)
        return listText





    }


}





