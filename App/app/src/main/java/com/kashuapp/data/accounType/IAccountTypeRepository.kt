package com.kashuapp.data.accounType

interface IAccountTypeRepository {


    suspend fun getAllAccountTypes(): Result<List<AccountType>>

}


