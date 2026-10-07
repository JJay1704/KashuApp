package com.kashuapp.data.account

interface IAccountRepository {


    suspend fun getAllAccount(userId : String): Result<List<Account>>
    suspend fun createAccount(account: Account): Result<Account>
    suspend fun updateAccount(account: Account): Result<Unit>
    suspend fun deleteAccount(accountId : String): Result<Unit>



}


