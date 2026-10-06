package com.kashuapp.data.transaction


interface ITransactionRepository {


    suspend fun getAll(): Result<List<Transaction>>
    suspend fun postTrans(trans: Transaction): Result<Unit>


}


