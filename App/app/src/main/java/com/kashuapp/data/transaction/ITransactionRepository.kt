package com.kashuapp.data.transaction


interface ITransactionRepository {


    suspend fun getAll(userId: String, accountId: String?): Result<List<Transaction>>
    suspend fun postTrans(trans: Transaction): Result<Unit>


}


