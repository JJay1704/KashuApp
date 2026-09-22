package com.kashuapp.data.accounts

interface IAccountRepository {


    suspend fun getAllAccount(): Result<List<Account>>


}


